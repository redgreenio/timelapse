package liftoff.affectedfiles.props.mobius

import com.spotify.mobius.Next
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEffect.GetCommitsAffectingFilePath
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEffect.GetTrackedFiles
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEffect.NotifyCommitSelected
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEvent.AffectingCommitSelected
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEvent.CommitsAffectingFilePathFetched
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEvent.FilePathSelected
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEvent.GitRepoSelected
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEvent.GitReposFound
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEvent.TrackedFilePathsFetched

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
