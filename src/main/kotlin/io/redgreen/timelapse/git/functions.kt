package io.redgreen.timelapse.git

import io.redgreen.timelapse.git.FileChange.Addition
import io.redgreen.timelapse.git.FileChange.Deletion
import io.redgreen.timelapse.git.FileChange.Modification
import io.redgreen.timelapse.git.FileChange.Rename
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffEntry.ChangeType.ADD
import org.eclipse.jgit.diff.DiffEntry.ChangeType.DELETE
import org.eclipse.jgit.diff.DiffEntry.ChangeType.MODIFY
import org.eclipse.jgit.diff.DiffEntry.ChangeType.RENAME
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RenameDetector
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.util.io.DisabledOutputStream.INSTANCE

private const val DEV_NULL_ID = "0000000000000000000000000000000000000000"

fun Repository.getFilesInCommit(commitId: String): List<FileChange> {
  val commit = RevWalk(this).use { it.parseCommit(ObjectId.fromString(commitId)) }
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
): List<FileChange> {
  val objectReader = newObjectReader()
  val formatter = DiffFormatter(INSTANCE)
  formatter.setRepository(this)

  val diffEntries = formatter
    .scan(getTreeParser(ancestor.tree, objectReader), getTreeParser(descendant.tree, objectReader))

  RenameDetector(this).apply {
    addAll(diffEntries)
    compute()
  }

  val additionsAndRenames = diffEntries
    .filter { it.changeType == RENAME || it.changeType == ADD }
    .groupBy { if (it.oldId.name() == DEV_NULL_ID) it.newId.name() else it.oldId.name() }

  val renames = additionsAndRenames
    .entries
    .map { it.value }
    .filter { it.size == 2 }
    .map { (entryA, entryB) ->
      val (renameEntry, addEntry) = if (entryA.changeType == RENAME) {
        entryA to entryB
      } else {
        entryB to entryA
      }

      Rename(addEntry.newPath, renameEntry.oldPath)
    }

  return if (renames.isNotEmpty()) {
    renames
  } else {
    diffEntries
      .filter { it.changeType != RENAME }
      .map(::toFileChange)
  }
}

private fun getTreeParser(
  tree: RevTree,
  objectReader: ObjectReader
): CanonicalTreeParser {
  return CanonicalTreeParser().apply {
    reset(objectReader, tree)
  }
}

private fun toFileChange(entry: DiffEntry): FileChange {
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
