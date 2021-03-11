package io.redgreen.timelapse.git

import arrow.core.Either
import io.redgreen.timelapse.git.model.AffectedFile
import io.redgreen.timelapse.git.model.CommitHash
import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.git.usecases.GetAffectedFilesUseCase
import io.redgreen.timelapse.vcs.ChangedFile
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode.REMOTE
import org.eclipse.jgit.lib.Repository

fun Repository.listRemoteBranches(): List<Branch> {
  return Git(this)
    .branchList()
    .setListMode(REMOTE)
    .call()
    .map { it.name to it.target.objectId.name }
    .map { (name, commitId) -> Branch(name, commitId) }
}

@Deprecated("Use [GetAffectedFilesUseCase] instead.")
fun Repository.getChangedFilesInCommit(commitId: String): List<ChangedFile> {
  val gitDirectory = GitDirectory.from(this.directory.absolutePath).get()
  val ancestorCommitHash = CommitHash(resolve("$commitId^1")?.name ?: commitId)
  val affectedFilesEither = GetAffectedFilesUseCase()
    .invoke(gitDirectory, CommitHash(commitId), ancestorCommitHash)
  if (affectedFilesEither is Either.Left) {
    throw affectedFilesEither.a
  }

  val affectedFiles = (affectedFilesEither as Either.Right).b
  return affectedFiles.map {
    when (it) {
      is AffectedFile.Added -> ChangedFile.Addition(it.filePath.value)
      is AffectedFile.Deleted -> ChangedFile.Deletion(it.filePath.value)
      is AffectedFile.Modified -> ChangedFile.Modification(it.filePath.value)
      is AffectedFile.Moved -> ChangedFile.Rename(it.filePath.value, it.oldFilePath.value)
    }
  }
}
