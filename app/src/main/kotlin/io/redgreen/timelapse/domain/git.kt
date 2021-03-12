package io.redgreen.timelapse.domain

import io.redgreen.timelapse.domain.BlobDiff.Merge
import io.redgreen.timelapse.domain.BlobDiff.Simple
import io.redgreen.timelapse.git.model.GitDirectory
import java.io.ByteArrayOutputStream
import java.io.File
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.Constants.HEAD
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathFilter

fun openGitRepository(gitDirectory: GitDirectory): Repository {
  return RepositoryBuilder().setGitDir(File(gitDirectory.path)).build()
}

fun Repository.getBlobDiff(
  commitId: String,
  filePath: String
): BlobDiff {
  val commit = getCommit(commitId)
  val parentCount = commit.parentCount
  val isInitialCommit = parentCount == 0
  val isMergeCommit = parentCount > 1

  val parentCommitIds = if (isInitialCommit) listOf(commitId) else commit.parents.map(RevCommit::name).toList()
  val simpleDiffs = parentCommitIds
    .map { parentCommitId -> Simple(parentCommitId, getBlobDiffBetweenCommits(filePath, parentCommitId, commit.name)) }

  return if (isMergeCommit) {
    Merge(simpleDiffs)
  } else {
    simpleDiffs.first()
  }
}

private fun Repository.getBlobDiffBetweenCommits(
  filePath: String,
  oldCommitId: String,
  newCommitId: String
): String {
  val filePathFilter = PathFilter.create(filePath)
  val outputStream = ByteArrayOutputStream()

  outputStream.use { diffOutputStream ->
    DiffFormatter(diffOutputStream).use { diffFormatter ->
      diffFormatter.setRepository(this)
      diffFormatter.pathFilter = filePathFilter

      /* This is a hack because git log output gives short commit hashes. */
      val isFirstCommit = /* oldCommitId == newCommitId || */ newCommitId.startsWith(oldCommitId)

      val oldTree = if (isFirstCommit) null else getTree(oldCommitId)
      val newTree = getTree(newCommitId)

      val diffEntry = diffFormatter.scan(oldTree, newTree).first()
      diffFormatter.format(diffEntry)
      diffFormatter.flush()

      return outputStream.toString()
    }
  }
}

fun Repository.getCommit(commitId: String): RevCommit {
  RevWalk(this).use { walk ->
    return walk.parseCommit(this.resolve(commitId))
  }
}

fun Repository.getFilePaths(): List<String> {
  val filePaths = mutableListOf<String>()

  val headObjectId = this.getHead()
  val headCommit = this.parseCommit(headObjectId)
  TreeWalk(this).use { treeWalk ->
    treeWalk.reset(headCommit.tree.id)
    treeWalk.isRecursive = true
    while (treeWalk.next()) {
      filePaths.add(treeWalk.pathString)
    }
  }

  return filePaths
}

private fun Repository.getHead(): ObjectId {
  return this.resolve(HEAD)
}

private fun Repository.getTree(commitId: String): RevTree {
  val revWalk = RevWalk(this)
  return revWalk.parseCommit(this.resolve(commitId)).tree
}
