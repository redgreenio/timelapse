package io.redgreen.timelapse.vcs.git

import io.reactivex.rxjava3.core.Single
import io.redgreen.timelapse.contentviewer.data.BlobDiffInformation
import io.redgreen.timelapse.diff.DiffLine
import io.redgreen.timelapse.diff.FormattedDiff
import io.redgreen.timelapse.domain.BlobDiff
import io.redgreen.timelapse.domain.BlobDiff.Merge
import io.redgreen.timelapse.domain.BlobDiff.Simple
import io.redgreen.timelapse.domain.getBlobDiff
import io.redgreen.timelapse.foo.debug
import io.redgreen.timelapse.git.getChangedFilesInCommit
import io.redgreen.timelapse.git.model.Identity
import io.redgreen.timelapse.vcs.ChangedFile
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import io.redgreen.timelapse.vcs.Contribution
import io.redgreen.timelapse.vcs.VcsRepositoryService
import org.eclipse.jgit.api.BlameCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry.ChangeType.DELETE
import org.eclipse.jgit.errors.MissingObjectException
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathFilter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.ZoneId
import java.util.Optional

private typealias ContributionCount = Int

class GitRepositoryService(private val gitRepository: Repository) : VcsRepositoryService {
  override fun getChangedFiles(commitId: String): Single<List<ChangedFile>> {
    return Single.create { emitter ->
      val revCommitOptional = gitRepository.getRevCommit(commitId)
      if (!revCommitOptional.isPresent) {
        emitter.onError(IllegalArgumentException("Invalid commit ID: $commitId"))
        return@create
      }

      emitter.onSuccess(gitRepository.getChangedFilesInCommit(commitId))
    }
  }

  // TODO: 30-10-2020 Refactor this function, it's really large 
  override fun getContributions(
    commitId: String,
    filePath: String
  ): Single<List<Contribution>> {
    return Single.create { emitter ->
      val revCommit = gitRepository.getRevCommit(commitId)
      if (!revCommit.isPresent) {
        emitter.onError(IllegalArgumentException("Invalid commit ID: $commitId"))
        return@create
      }

      val blameCommand = BlameCommand(gitRepository).apply {
        setStartCommit(revCommit.get())
        setFilePath(filePath)
      }
      val blameResult = blameCommand.call()

      val contributionsCountMap = mutableMapOf<Identity, ContributionCount>()
      val lineCount = try {
        gitRepository.countLinesInFile(revCommit.get(), filePath)
      } catch (exception: MissingObjectException) {
        emitter.onError(IllegalArgumentException("Non-existent file path at $commitId: $filePath", exception))
        return@create
      }
      for (index in 0 until lineCount) {
        val sourceAuthor = blameResult.getSourceAuthor(index)
        val authorIdentity = Identity(sourceAuthor.name, sourceAuthor.emailAddress)

        with(contributionsCountMap) {
          computeIfPresent(authorIdentity) { _, contributionCount -> contributionCount + 1 }
          putIfAbsent(authorIdentity, 1)
        }
      }

      val contributions = contributionsCountMap.map { (identity, contributionCount) ->
        Contribution(identity, contributionCount / lineCount.toDouble())
      }

      emitter.onSuccess(contributions.sortedByDescending { it.fraction })
    }
  }

  override fun getFirstCommitOnOrAfter(date: LocalDate): Single<String> {
    return Single.create { emitter ->
      val allRefs = gitRepository.refDatabase.refs
      RevWalk(gitRepository).use { revWalk ->
        revWalk.markStart(revWalk.parseCommit(allRefs.first().objectId))

        var commitId: String? = null
        for (commit in revWalk) {
          val firstSecondOfDate = date.startDateMillis()
          val lastSecondOfDate = date.endDateMillis()

          val commitTime = commit.authorIdent.`when`.time
          val commitIsFromDateRequested = commitTime in firstSecondOfDate..lastSecondOfDate
          val commitAfterDateRequested = commitTime > firstSecondOfDate
          if (commitIsFromDateRequested || commitAfterDateRequested) {
            commitId = commit.name
          }
        }
        emitter.onSuccess(commitId!!)
      }
    }
  }

  private fun LocalDate.startDateMillis(): Long {
    return this
      .atTime(0, 0, 0, 0)
      .atZone(ZoneId.systemDefault())
      .toInstant()
      .toEpochMilli()
  }

  private fun LocalDate.endDateMillis(): Long {
    return this
      .atTime(23, 59, 59, 999_999_999)
      .atZone(ZoneId.systemDefault())
      .toInstant()
      .toEpochMilli()
  }

  override fun getChangedFilePaths(
    descendantCommitId: String,
    ancestorCommitId: String?
  ): Single<List<String>> {
    debug { "Getting changed files between $ancestorCommitId and $descendantCommitId" }

    return Single.create { emitter ->
      val ancestorObjectId = gitRepository.getParentObjectId(descendantCommitId)

      val changedFilePaths = if (ancestorObjectId == null) {
        gitRepository
          .getFilesFromInitialCommit(gitRepository.parseCommit(gitRepository.resolve(descendantCommitId)))
          .map(ChangedFile::filePath)
          .distinct()
      } else {
        val oldTree = gitRepository.parseCommit(gitRepository.resolve(ancestorCommitId)).tree
        val newTree = gitRepository.parseCommit(gitRepository.resolve(descendantCommitId)).tree
        val objectReader = gitRepository.newObjectReader()
        val diffCommand = Git(gitRepository)
          .diff()
          .setOldTree(oldTree.getTreeParser(objectReader))
          .setNewTree(newTree.getTreeParser(objectReader))
        val diffEntry = diffCommand.call()

        diffEntry
          .filter { it.changeType != DELETE }
          .map { it.newPath }
      }

      emitter.onSuccess(changedFilePaths)
    }
  }

  override fun getBlobDiff(
    selectedFilePath: String,
    commitId: String
  ): Single<BlobDiff> {
    return Single.create { emitter ->
      try {
        val blobDiff = gitRepository.getBlobDiff(commitId, selectedFilePath)
        emitter.onSuccess(blobDiff)
      } catch (e: NoSuchElementException) {
        emitter.onError(IllegalArgumentException("File path does not exist: $selectedFilePath", e))
      } catch (e: NullPointerException) {
        emitter.onError(IllegalArgumentException("Invalid commit ID: $commitId", e))
      }
    }
  }

  override fun getBlobDiffInformation(
    selectedFilePath: String,
    commitId: String
  ): Single<BlobDiffInformation> {
    return Single.create { emitter ->
      try {
        val changedFilesInCommit = gitRepository.getChangedFilesInCommit(commitId)
        val changedFilesCount = changedFilesInCommit.size
        val message = gitRepository.getRevCommit(commitId).get().shortMessage
        val blobDiff = getBlobDiff(selectedFilePath, commitId).blockingGet()
        val diffLines = if (blobDiff is Simple) {
          FormattedDiff.from(blobDiff.rawDiff).lines
        } else {
          FormattedDiff.from((blobDiff as Merge).diffs.first().rawDiff).lines
        }
        val deletions = diffLines.filterIsInstance<DiffLine.Deletion>().size
        val insertions = diffLines.filterIsInstance<DiffLine.Insertion>().size

        val blobDiffInformation = BlobDiffInformation(
          selectedFilePath,
          commitId,
          message,
          deletions,
          insertions,
          changedFilesCount
        )
        emitter.onSuccess(blobDiffInformation)
      } catch (e: NullPointerException) {
        emitter.onError(IllegalArgumentException("Invalid commit ID: $commitId", e))
      } catch (e: IllegalArgumentException) {
        emitter.onError(e)
      }
    }
  }

  private fun Repository.getRevCommit(commitId: String): Optional<RevCommit> {
    val resolvedObjectId = resolve(commitId) ?: return Optional.empty()

    RevWalk(gitRepository).use { revWalk ->
      return Optional.of(revWalk.parseCommit(resolvedObjectId))
    }
  }

  private fun RevTree.getTreeParser(
    objectReader: ObjectReader
  ): CanonicalTreeParser {
    val treeParser = CanonicalTreeParser()
    treeParser.reset(objectReader, this)
    return treeParser
  }

  private fun Repository.getFilesFromInitialCommit(
    revCommit: RevCommit
  ): List<ChangedFile> {
    val addedFiles = mutableListOf<ChangedFile>()
    TreeWalk(this).use { treeWalk ->
      treeWalk.addTree(revCommit.tree)
      treeWalk.isRecursive = true
      while (treeWalk.next()) {
        addedFiles.add(Addition(treeWalk.pathString))
      }
    }
    return addedFiles.toList()
  }

  private fun Repository.countLinesInFile(commitId: ObjectId, filePath: String): Int {
    val byteArrayOutputStream = ByteArrayOutputStream()

    RevWalk(this).use { revWalk ->
      val commit = revWalk.parseCommit(commitId)
      TreeWalk(this).use { treeWalk ->
        with(treeWalk) {
          addTree(commit.tree)
          isRecursive = true
          filter = PathFilter.create(filePath)
          next()

          val fileObjectId = treeWalk.getObjectId(0)
          val loader = open(fileObjectId)

          loader.copyTo(byteArrayOutputStream)
        }
      }
      revWalk.dispose()
    }

    return ByteArrayInputStream(byteArrayOutputStream.toByteArray())
      .reader(Charset.forName("UTF-8"))
      .readLines()
      .size
  }

  private fun Repository.getParentObjectId(commitId: String): ObjectId? =
    resolve("$commitId^")
}
