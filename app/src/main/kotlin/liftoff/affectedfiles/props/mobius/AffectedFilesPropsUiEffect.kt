package liftoff.affectedfiles.props.mobius

import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.git.model.TrackedFilePath
import liftoff.affectedfiles.model.AffectingCommit

sealed class AffectedFilesPropsUiEffect {
  data class DiscoverGitRepos(
    val directoryPath: String
  ) : AffectedFilesPropsUiEffect()

  data class GetTrackedFiles(
    val gitDirectory: String
  ) : AffectedFilesPropsUiEffect()

  data class GetCommitsAffectingFilePath(
    val gitDirectory: String,
    val filePath: TrackedFilePath
  ) : AffectedFilesPropsUiEffect()

  data class NotifyCommitSelected(
    val gitDirectory: GitDirectory,
    val filePath: TrackedFilePath,
    val affectingCommit: AffectingCommit
  ) : AffectedFilesPropsUiEffect()
}
