package toys.affectedfiles.model;

import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.timelapse.core.TrackedFilePath

sealed class AffectedFilesEffect {
  data class DiscoverGitRepos(
    val directoryPath: String
  ) : AffectedFilesEffect()

  data class GetTrackedFiles(
    val gitDirectory: String
  ) : AffectedFilesEffect()

  data class GetCommitsAffectingFilePath(
    val gitDirectory: String,
    val filePath: TrackedFilePath
  ) : AffectedFilesEffect()

  data class NotifyCommitSelected(
    val gitRepo: GitRepo,
    val filePath: TrackedFilePath,
    val affectingCommit: AffectingCommit
  ) : AffectedFilesEffect()
}
