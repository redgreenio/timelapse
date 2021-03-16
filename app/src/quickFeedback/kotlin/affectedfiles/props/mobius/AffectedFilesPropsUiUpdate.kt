package affectedfiles.props.mobius

import affectedfiles.props.mobius.AffectedFilesPropsUiEffect.GetCommitsAffectingFilePath
import affectedfiles.props.mobius.AffectedFilesPropsUiEffect.GetTrackedFiles
import affectedfiles.props.mobius.AffectedFilesPropsUiEffect.NotifyCommitSelected
import affectedfiles.props.mobius.AffectedFilesPropsUiEvent.AffectingCommitSelected
import affectedfiles.props.mobius.AffectedFilesPropsUiEvent.CommitsAffectingFilePathFetched
import affectedfiles.props.mobius.AffectedFilesPropsUiEvent.FilePathSelected
import affectedfiles.props.mobius.AffectedFilesPropsUiEvent.GitRepoSelected
import affectedfiles.props.mobius.AffectedFilesPropsUiEvent.GitReposFound
import affectedfiles.props.mobius.AffectedFilesPropsUiEvent.TrackedFilePathsFetched
import com.spotify.mobius.Next
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update

object AffectedFilesPropsUiUpdate :
  Update<AffectedFilesPropsUiModel, AffectedFilesPropsUiEvent, AffectedFilesPropsUiEffect> {

  override fun update(
    model: AffectedFilesPropsUiModel,
    event: AffectedFilesPropsUiEvent
  ): Next<AffectedFilesPropsUiModel, AffectedFilesPropsUiEffect> {
    return when (event) {
      is GitReposFound -> next(model.gitDirectoriesFound(event.gitDirectories))

      is GitRepoSelected -> next(
        model.gitDirectorySelected(event.gitDirectory),
        setOf(GetTrackedFiles(event.gitDirectory.path))
      )

      is TrackedFilePathsFetched -> next(
        model.trackedFilePathsFetched(event.trackedFilePaths)
      )

      is FilePathSelected -> {
        val gitDirectory = model.selectedGitDirectory.get().path
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
            model.selectedGitDirectory.get(),
            model.selectedFilePath.get(),
            event.affectingCommit
          )
        )
      )
    }
  }
}
