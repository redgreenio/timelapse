package io.redgreen.timelapse.vcs.git

import io.reactivex.rxjava3.core.Single
import io.redgreen.timelapse.vcs.ChangedFile
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import io.redgreen.timelapse.vcs.ChangedFile.Deletion
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import io.redgreen.timelapse.vcs.VcsRepositoryService
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffEntry.ChangeType.ADD
import org.eclipse.jgit.diff.DiffEntry.ChangeType.DELETE
import org.eclipse.jgit.diff.DiffEntry.ChangeType.MODIFY
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RenameDetector
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.util.io.DisabledOutputStream
import java.util.Optional

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

      val objectId = gitRepository.resolve("$commitId^")
      val parentCommit = objectId?.let { gitRepository.parseCommit(it) }

      val changedFiles = if (parentCommit != null) {
        gitRepository.getFilesBetweenCommits(parentCommit, revCommitOptional.get())
      } else {
        gitRepository.getFilesFromInitialCommit(revCommitOptional.get())
      }
      emitter.onSuccess(changedFiles)
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
}
