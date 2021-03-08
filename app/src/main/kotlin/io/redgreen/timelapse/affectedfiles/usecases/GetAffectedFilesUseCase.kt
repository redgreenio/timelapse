package io.redgreen.timelapse.affectedfiles.usecases

import arrow.core.Either
import arrow.core.Either.Companion.left
import arrow.core.Either.Companion.right
import io.redgreen.timelapse.affectedfiles.model.AffectedFile
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Added
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Deleted
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Modified
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Moved
import io.redgreen.timelapse.core.CommitHash
import io.redgreen.timelapse.core.GitDirectory
import io.redgreen.timelapse.core.TrackedFilePath
import io.redgreen.timelapse.extensions.isDelete
import io.redgreen.timelapse.extensions.isInsert
import io.redgreen.timelapse.extensions.isReplace
import java.io.File
import java.time.Duration
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
  data class Metric(val duration: Duration)

  fun invoke(
    gitDirectory: GitDirectory,
    descendent: CommitHash,
    ancestor: CommitHash
  ): Either<IllegalArgumentException, List<AffectedFile>> {
    val repository = RepositoryBuilder().setGitDir(File(gitDirectory.path)).build()

    return try {
      right(getAffectedFiles(repository, descendent, ancestor))
    } catch (exception: IllegalArgumentException) {
      left(exception)
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
    COPY, ADD -> Added(TrackedFilePath(entry.newPath), calculateInsertions(header))
    MODIFY -> Modified(TrackedFilePath(entry.newPath), calculateDeletions(header), calculateInsertions(header))
    DELETE -> Deleted(TrackedFilePath(entry.oldPath), calculateDeletions(header))
    RENAME -> toMoved(entry.newPath, entry.oldPath, header)
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

  private fun toMoved(
    newPath: String,
    oldPath: String,
    header: FileHeader
  ): Moved {
    return Moved(
      TrackedFilePath(newPath),
      TrackedFilePath(oldPath),
      calculateDeletions(header),
      calculateInsertions(header)
    )
  }
}
