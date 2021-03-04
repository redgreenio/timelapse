package io.redgreen.timelapse.affectedfiles.usecases

import io.reactivex.rxjava3.core.Single
import io.redgreen.timelapse.affectedfiles.model.AffectedFile
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Deleted
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Modified
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Moved
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.New
import io.redgreen.timelapse.extensions.isDelete
import io.redgreen.timelapse.extensions.isInsert
import io.redgreen.timelapse.extensions.isReplace
import io.redgreen.timelapse.git.CommitHash
import java.io.File
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffEntry.ChangeType.ADD
import org.eclipse.jgit.diff.DiffEntry.ChangeType.COPY
import org.eclipse.jgit.diff.DiffEntry.ChangeType.DELETE
import org.eclipse.jgit.diff.DiffEntry.ChangeType.MODIFY
import org.eclipse.jgit.diff.DiffEntry.ChangeType.RENAME
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.Edit
import org.eclipse.jgit.diff.RenameDetector
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.patch.FileHeader
import org.eclipse.jgit.patch.HunkHeader
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.EmptyTreeIterator
import org.eclipse.jgit.util.io.DisabledOutputStream

class GetAffectedFilesUseCase {
  fun invoke(
    repositoryPath: String,
    descendent: CommitHash,
    ancestor: CommitHash
  ): Single<List<AffectedFile>> {
    // TODO: 04/03/21 Introduce a type to distinguish between project and git directories
    return Single.create { emitter ->
      val repository = RepositoryBuilder().setGitDir(File(repositoryPath)).build()

      try {
        val affectedFiles = getAffectedFiles(repository, descendent, ancestor)
        emitter.onSuccess(affectedFiles)
      } catch (exception: IllegalStateException) {
        emitter.onError(exception)
      }
    }
  }

  private fun getAffectedFiles(
    repository: Repository,
    descendent: CommitHash,
    ancestor: CommitHash
  ): List<AffectedFile> {
    val isInitialCommit = ancestor == descendent
    val objectReader = repository.newObjectReader()
    return if (isInitialCommit) {
      getAffectedFilesForInitialCommit(repository, objectReader, descendent)
    } else {
      getAffectedFilesForNonInitialCommit(repository, objectReader, descendent, ancestor)
    }
  }

  private fun getAffectedFilesForInitialCommit(
    repository: Repository,
    objectReader: ObjectReader,
    descendent: CommitHash
  ): List<AffectedFile> {
    val descendentObjectId = repository.resolve(descendent.value)
      ?: throw IllegalArgumentException("Invalid descendent commit ID: ${descendent.value}")

    val descendentTree = repository.parseCommit(descendentObjectId).tree
    val descendentParser = CanonicalTreeParser(null, objectReader, descendentTree)
    val ancestorIterator = EmptyTreeIterator()
    return computeDiffForInitialCommit(repository, ancestorIterator, descendentParser)
  }

  private fun getAffectedFilesForNonInitialCommit(
    repository: Repository,
    objectReader: ObjectReader,
    descendent: CommitHash,
    ancestor: CommitHash
  ): List<AffectedFile> {
    val descendentParser = CanonicalTreeParser().apply {
      val descendentObjectId = repository.resolve(descendent.value)
        ?: throw IllegalArgumentException("Invalid descendent commit ID: ${descendent.value}")
      val descendentTree = repository.parseCommit(descendentObjectId).tree
      reset(objectReader, descendentTree)
    }

    val ancestorParser = CanonicalTreeParser().apply {
      val ancestorObjectId = repository.resolve(ancestor.value)
        ?: throw IllegalArgumentException("Invalid ancestor commit ID: ${ancestor.value}")
      val ancestorTree = repository.parseCommit(ancestorObjectId).tree
      reset(objectReader, ancestorTree)
    }

    return computeDiffForNonInitialCommit(repository, ancestorParser, descendentParser)
  }

  private fun computeDiffForInitialCommit(
    repository: Repository,
    ancestorIterator: EmptyTreeIterator,
    descendentParser: CanonicalTreeParser
  ): List<AffectedFile> {
    val diffFormatter = getDiffFormatter(repository)

    val diffEntries = diffFormatter.scan(ancestorIterator, descendentParser).filterNotNull()
    val diffEntryFileHeaders = diffEntries.mapNotNull { diffFormatter.toFileHeader(it) }

    return diffEntries.zip(diffEntryFileHeaders, this::mapDiffEntryToAffectedFile)
  }

  private fun computeDiffForNonInitialCommit(
    repository: Repository,
    ancestorParser: CanonicalTreeParser,
    descendentParser: CanonicalTreeParser
  ): List<AffectedFile> {
    val diffFormatter = getDiffFormatter(repository)

    val diffEntries = RenameDetector(repository)
      .apply { addAll(diffFormatter.scan(ancestorParser, descendentParser)) }
      .compute()
      .filterNotNull()
    val diffEntryFileHeaders = diffEntries.mapNotNull { diffFormatter.toFileHeader(it) }

    return diffEntries.zip(diffEntryFileHeaders, this::mapDiffEntryToAffectedFile)
  }

  private fun getDiffFormatter(repository: Repository): DiffFormatter {
    return DiffFormatter(DisabledOutputStream.INSTANCE).apply {
      setRepository(repository)
      setContext(0)
    }
  }

  private fun mapDiffEntryToAffectedFile(
    entry: DiffEntry,
    header: FileHeader
  ): AffectedFile = when (entry.changeType) {
    COPY, ADD -> New(entry.newPath, calculateInsertions(header))
    MODIFY -> Modified(entry.newPath, calculateDeletions(header), calculateInsertions(header))
    DELETE -> Deleted(entry.oldPath, calculateDeletions(header))
    RENAME -> Moved(entry.newPath, entry.oldPath, calculateDeletions(header), calculateInsertions(header))
    else -> throw IllegalStateException("Unknown `changeType`: ${entry.changeType}")
  }

  private fun calculateDeletions(
    header: FileHeader
  ): Int {
    return getEdits(header)
      .filter { it.isDelete() || it.isReplace() }
      .sumBy { it.endA - it.beginA }
  }

  private fun calculateInsertions(
    header: FileHeader
  ): Int {
    return getEdits(header)
      .filter { it.isInsert() || it.isReplace() }
      .sumBy { it.endB - it.beginB }
  }

  private fun getEdits(
    header: FileHeader
  ): List<Edit> {
    return header
      .hunks
      .map(HunkHeader::toEditList)
      .flatten()
  }
}
