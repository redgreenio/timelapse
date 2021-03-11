package liftoff.affectedfiles.props.mobius

import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.git.model.TrackedFilePath
import liftoff.affectedfiles.model.AffectingCommit

sealed class AffectedFilesPropsUiEvent {
  data class GitReposFound(
    val gitDirectories: List<GitDirectory>
  ) : AffectedFilesPropsUiEvent()

  data class GitRepoSelected(
    val gitDirectory: GitDirectory
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
