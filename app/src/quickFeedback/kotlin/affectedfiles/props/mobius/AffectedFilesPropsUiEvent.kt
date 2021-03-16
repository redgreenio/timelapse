package affectedfiles.props.mobius

import affectedfiles.model.AffectingCommit
import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.git.model.TrackedFilePath

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
