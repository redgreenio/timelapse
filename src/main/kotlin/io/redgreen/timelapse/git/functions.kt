package io.redgreen.timelapse.git

import io.redgreen.timelapse.vcs.ChangedFile
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import io.redgreen.timelapse.vcs.ChangedFile.Deletion
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import io.redgreen.timelapse.vcs.ChangedFile.Rename
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffEntry.ChangeType.ADD
import org.eclipse.jgit.diff.DiffEntry.ChangeType.DELETE
import org.eclipse.jgit.diff.DiffEntry.ChangeType.MODIFY
import org.eclipse.jgit.diff.DiffEntry.ChangeType.RENAME
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RenameDetector
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.util.io.DisabledOutputStream.INSTANCE
import kotlin.collections.Map.Entry

private const val DEV_NULL_ID = "0000000000000000000000000000000000000000"

fun Repository.getChangedFilesInCommit(commitId: String): List<ChangedFile> {
  val commit = RevWalk(this).use { it.parseCommit(resolve(commitId)) }
  val objectId = resolve("$commitId^")
  val parentCommit = objectId?.let { parseCommit(it) }

  return if (parentCommit != null) {
    getFilesBetweenCommits(parentCommit, commit)
  } else {
    getFilesFromInitialCommit(commit)
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

  val formatter = DiffFormatter(INSTANCE)
  formatter.setRepository(this)

  val diffEntries = formatter
    .scan(getTreeParser(ancestor.tree, objectReader), getTreeParser(descendant.tree, objectReader))

  if (detectRenames) {
    RenameDetector(this).apply {
      addAll(diffEntries)
      compute()
    }
  }

  return diffEntries
}

private fun groupAdditionsAndRenames(
  diffEntries: MutableList<DiffEntry>
): Map<String, List<DiffEntry>> {
  return diffEntries
    .filter { it.changeType == RENAME || it.changeType == ADD }
    .groupBy { if (it.oldId.name() == DEV_NULL_ID) it.newId.name() else it.oldId.name() }
}

private fun pairAdditionsAndRenames(
  additionsAndRenames: Map<String, List<DiffEntry>>
): List<Pair<DiffEntry, DiffEntry>> {
  return additionsAndRenames
    .entries
    .map(Entry<String, List<DiffEntry>>::value)
    .filter { it.size >= 2 }
    .map { (entryA, entryB) ->
      val (renameEntry, addEntry) = if (entryA.changeType == RENAME) {
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

private fun getRenames(addRenameEntryPairs: List<Pair<DiffEntry, DiffEntry>>): List<Rename> {
  return addRenameEntryPairs
    .map { (add, rename) -> Rename(add.newPath, rename.oldPath) }
}

private fun getFileChangesExcludingRenames(
  diffEntries: MutableList<DiffEntry>
): List<ChangedFile> {
  return diffEntries
    .filter { it.changeType != RENAME }
    .map(::toChangedFile)
}

private fun getTreeParser(
  tree: RevTree,
  objectReader: ObjectReader
): CanonicalTreeParser {
  return CanonicalTreeParser().apply {
    reset(objectReader, tree)
  }
}

private fun toChangedFile(entry: DiffEntry): ChangedFile {
  return when (entry.changeType) {
    ADD -> Addition(entry.newPath)
    MODIFY -> Modification(entry.newPath)
    DELETE -> Deletion(entry.oldPath)
    else -> TODO("Yet to add support for ${entry.changeType}")
  }
}

private fun Repository.getFilesFromInitialCommit(
  commit: RevCommit
): List<Addition> {
  val addedFiles = mutableListOf<Addition>()
  TreeWalk(this).use { treeWalk ->
    treeWalk.addTree(commit.tree)
    treeWalk.isRecursive = true
    while (treeWalk.next()) {
      addedFiles.add(Addition(treeWalk.pathString))
    }
  }
  return addedFiles.toList()
}
