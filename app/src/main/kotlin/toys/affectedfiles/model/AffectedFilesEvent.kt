package toys.affectedfiles.model

import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.timelapse.core.TrackedFilePath

sealed class AffectedFilesEvent {
  data class GitReposFound(
    val gitRepos: List<GitRepo>
  ) : AffectedFilesEvent()

  data class GitRepoSelected(
    val gitRepo: GitRepo
  ) : AffectedFilesEvent()

  data class TrackedFilePathsFetched(
    val trackedFilePaths: List<TrackedFilePath>
  ) : AffectedFilesEvent()

  data class FilePathSelected(
    val filePath: TrackedFilePath
  ) : AffectedFilesEvent()

  data class CommitsAffectingFilePathFetched(
    val affectingCommits: List<AffectingCommit>
  ) : AffectedFilesEvent()

  data class AffectingCommitSelected(
    val affectingCommit: AffectingCommit
  ) : AffectedFilesEvent()
}
