package liftoff.affectedfiles.props.mobius

import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.timelapse.git.model.TrackedFilePath
import liftoff.affectedfiles.model.AffectingCommit

sealed class AffectedFilesPropsUiEvent {
  data class GitReposFound(
    val gitRepos: List<GitRepo>
  ) : AffectedFilesPropsUiEvent()

  data class GitRepoSelected(
    val gitRepo: GitRepo
  ) : AffectedFilesPropsUiEvent()

  data class TrackedFilePathsFetched(
    val trackedFilePaths: List<TrackedFilePath>
  ) : AffectedFilesPropsUiEvent()

  data class FilePathSelected(
    val filePath: TrackedFilePath
  ) : AffectedFilesPropsUiEvent()

  data class CommitsAffectingFilePathFetched(
    val affectingCommits: List<AffectingCommit>
  ) : AffectedFilesPropsUiEvent()

  data class AffectingCommitSelected(
    val affectingCommit: AffectingCommit
  ) : AffectedFilesPropsUiEvent()
}
