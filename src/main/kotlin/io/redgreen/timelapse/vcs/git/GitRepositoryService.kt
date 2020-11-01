package io.redgreen.timelapse.vcs.git

import io.reactivex.rxjava3.core.Single
import io.redgreen.timelapse.debug
import io.redgreen.timelapse.vcs.ChangedFile
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import io.redgreen.timelapse.vcs.ChangedFile.Deletion
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import io.redgreen.timelapse.vcs.Contribution
import io.redgreen.timelapse.vcs.Identity
import io.redgreen.timelapse.vcs.VcsRepositoryService
import org.eclipse.jgit.api.BlameCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffEntry.ChangeType.ADD
import org.eclipse.jgit.diff.DiffEntry.ChangeType.DELETE
import org.eclipse.jgit.diff.DiffEntry.ChangeType.MODIFY
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RenameDetector
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
import org.eclipse.jgit.util.io.DisabledOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.ZoneId
import java.util.Optional

private typealias ContributionCount = Int

class GitRepositoryService(private val gitRepository: Repository) : VcsRepositoryService {
  companion object {
    private const val DEV_NULL_ID = "0000000000000000000000000000000000000000"
  }

  override fun getChangedFiles(commitId: String): Single<List<ChangedFile>> {
    return Single.create { emitter ->
      val revCommitOptional = gitRepository.getRevCommit(commitId)
      if (!revCommitOptional.isPresent) {
        emitter.onError(IllegalArgumentException("Invalid commit ID: $commitId"))
        return@create
      }

      val objectId = gitRepository.getParentObjectId(commitId)
      val parentCommit = objectId?.let { gitRepository.parseCommit(it) }

      val changedFiles = if (parentCommit != null) {
        gitRepository.getFilesBetweenCommits(parentCommit, revCommitOptional.get())
      } else {
        gitRepository.getFilesFromInitialCommit(revCommitOptional.get())
      }
      emitter.onSuccess(changedFiles)
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
        emitter.onError(IllegalArgumentException("Non-existent file path at $commitId: $filePath"))
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
        allRefs.forEach { ref ->
          revWalk.markStart(revWalk.parseCommit(ref.objectId))
        }

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

  private fun Repository.getRevCommit(commitId: String): Optional<RevCommit> {
    val resolvedObjectId = resolve(commitId) ?: return Optional.empty()

    RevWalk(gitRepository).use { revWalk ->
      return Optional.of(revWalk.parseCommit(resolvedObjectId))
    }
  }

  private fun Repository.getFilesBetweenCommits(
    ancestor: RevCommit,
    descendant: RevCommit
  ): List<ChangedFile> {
    val diffEntries = getDiffEntries(ancestor, descendant, true)
    val additionsAndRenamesGroups = groupAdditionsAndRenames(diffEntries)
    val addRenameEntryPairs = pairAdditionsAndRenames(additionsAndRenamesGroups)
    val addEntriesPartOfRename = getAddEntriesPartOfRename(addRenameEntryPairs)
    val renames = getRenames(addRenameEntryPairs)

    return (getFileChangesExcludingRenames(diffEntries) - addEntriesPartOfRename + renames)
      .distinct()
  }

  private fun Repository.getDiffEntries(
    ancestor: RevCommit,
    descendant: RevCommit,
    detectRenames: Boolean
  ): MutableList<DiffEntry> {
    val objectReader = newObjectReader()

    val formatter = DiffFormatter(DisabledOutputStream.INSTANCE)
    formatter.setRepository(this)

    val diffEntries = formatter
      .scan(ancestor.tree.getTreeParser(objectReader), descendant.tree.getTreeParser(objectReader))

    if (detectRenames) {
      RenameDetector(this).apply {
        addAll(diffEntries)
        compute()
      }
    }

    return diffEntries
  }

  private fun RevTree.getTreeParser(
    objectReader: ObjectReader
  ): CanonicalTreeParser {
    val treeParser = CanonicalTreeParser()
    treeParser.reset(objectReader, this)
    return treeParser
  }

  private fun groupAdditionsAndRenames(
    diffEntries: MutableList<DiffEntry>
  ): Map<String, List<DiffEntry>> {
    return diffEntries
      .filter { it.changeType == DiffEntry.ChangeType.RENAME || it.changeType == ADD }
      .groupBy { if (it.oldId.name() == DEV_NULL_ID) it.newId.name() else it.oldId.name() }
  }

  private fun pairAdditionsAndRenames(
    additionsAndRenames: Map<String, List<DiffEntry>>
  ): List<Pair<DiffEntry, DiffEntry>> {
    return additionsAndRenames
      .entries
      .map(Map.Entry<String, List<DiffEntry>>::value)
      .filter { it.size >= 2 }
      .map { (entryA, entryB) ->
        val (renameEntry, addEntry) = if (entryA.changeType == DiffEntry.ChangeType.RENAME) {
          entryA to entryB
        } else {
          entryB to entryA
        }

        addEntry to renameEntry
      }
  }

  private fun getAddEntriesPartOfRename(
    addRenameEntryPairs: List<Pair<DiffEntry, DiffEntry>>
  ): List<Addition> {
    return addRenameEntryPairs
      .map(Pair<DiffEntry, DiffEntry>::first)
      .map { Addition(it.newPath) }
  }

  private fun getRenames(addRenameEntryPairs: List<Pair<DiffEntry, DiffEntry>>): List<ChangedFile.Rename> {
    return addRenameEntryPairs
      .map { (add, rename) -> ChangedFile.Rename(add.newPath, rename.oldPath) }
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

  private fun getFileChangesExcludingRenames(
    diffEntries: MutableList<DiffEntry>
  ): List<ChangedFile> {
    return diffEntries
      .filter { it.changeType != DiffEntry.ChangeType.RENAME }
      .map(::toChangedFile)
  }

  private fun toChangedFile(entry: DiffEntry): ChangedFile {
    return when (entry.changeType) {
      ADD -> Addition(entry.newPath)
      MODIFY -> Modification(entry.newPath)
      DELETE -> Deletion(entry.oldPath)
      else -> TODO("Yet to add support for ${entry.changeType}")
    }
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
