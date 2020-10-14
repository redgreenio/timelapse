package io.redgreen.timelapse.domain

import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.Constants.HEAD
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathFilter
import java.io.ByteArrayOutputStream
import java.io.File

fun openGitRepository(repositoryPath: File): Repository {
  val gitDirectory = "${repositoryPath.canonicalPath}${File.separator}.git"

  val repository = FileRepositoryBuilder()
    .setGitDir(File(gitDirectory))
    .build()

  return if (repository.branch != null) {
    repository
  } else {
    throw IllegalStateException("Not a git directory: ${repositoryPath.canonicalPath}")
  }
}

fun Repository.readFileFromCommitId(
  commitId: String,
  filePath: String
): String {
  val commitObjectId = this.resolve(commitId)
  var text: String
  RevWalk(this).use { revWalk ->
    val commit = revWalk.parseCommit(commitObjectId)
    val tree = commit.tree

    TreeWalk(this).apply {
      addTree(tree)
      isRecursive = true
      filter = PathFilter.create(filePath)
    }.use { treeWalk ->
      treeWalk.next()

      val filePathObjectId = treeWalk.getObjectId(0)
      val loader = this.open(filePathObjectId)

      text = String(loader.bytes)
    }
  }
  return text
}

fun Repository.getDiff(
  filePath: String,
  oldCommitId: String,
  newCommitId: String
): String {
  var diff: String

  val filePathFilter = PathFilter.create(filePath)
  val outputStream = ByteArrayOutputStream()

  outputStream.use { diffOutputStream ->
    DiffFormatter(diffOutputStream).use { diffFormatter ->
      diffFormatter.setRepository(this)
      diffFormatter.pathFilter = filePathFilter

      val oldTree = getTree(oldCommitId)
      val newTree = getTree(newCommitId)

      val diffEntry = diffFormatter.scan(oldTree, newTree).first()
      diffFormatter.format(diffEntry)
      diffFormatter.flush()

      diff = outputStream.toString()
    }
  }

  return diff
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
