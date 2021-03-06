package toys.affectedfiles.model

import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update
import toys.affectedfiles.model.AffectedFilesEffect.GetCommitsAffectingFilePath
import toys.affectedfiles.model.AffectedFilesEffect.GetTrackedFiles
import toys.affectedfiles.model.AffectedFilesEffect.NotifyCommitSelected
import toys.affectedfiles.model.AffectedFilesEvent.AffectingCommitSelected
import toys.affectedfiles.model.AffectedFilesEvent.CommitsAffectingFilePathFetched
import toys.affectedfiles.model.AffectedFilesEvent.FilePathSelected
import toys.affectedfiles.model.AffectedFilesEvent.GitRepoSelected
import toys.affectedfiles.model.AffectedFilesEvent.GitReposFound
import toys.affectedfiles.model.AffectedFilesEvent.TrackedFilePathsFetched

object AffectedFilesUpdate : Update<AffectedFilesModel, AffectedFilesEvent, AffectedFilesEffect> {
  override fun update(
    model: AffectedFilesModel,
    event: AffectedFilesEvent
  ): Next<AffectedFilesModel, AffectedFilesEffect> {
    return when (event) {
      is GitReposFound -> next(
        model.gitReposFound(event.gitRepos)
      )

      is GitRepoSelected -> next(
        model.gitRepoSelected(event.gitRepo),
        setOf(GetTrackedFiles(event.gitRepo.gitDirectory.absolutePath))
      )

      is TrackedFilePathsFetched -> next(
        model.trackedFilePathsFetched(event.trackedFilePaths)
      )

      is FilePathSelected -> {
        val gitDirectory = model.selectedGitRepo.get().gitDirectory.absolutePath
        next(
          model.filePathSelected(event.filePath),
          setOf(GetCommitsAffectingFilePath(gitDirectory, event.filePath))
        )
      }

      is CommitsAffectingFilePathFetched -> next(
        model.commitsAffectingFilePathFetched(event.affectingCommits)
      )

      is AffectingCommitSelected -> next(
        model.affectingCommitSelected(event.affectingCommit),
        setOf(
          NotifyCommitSelected(
            model.selectedGitRepo.get(),
            model.selectedFilePath.get(),
            event.affectingCommit
          )
        )
      )
    }
  }
}
