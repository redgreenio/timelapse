package toys.affectedfiles.model

import io.redgreen.architecture.mobius.AsyncOp
import io.redgreen.architecture.mobius.AsyncOp.Companion.content
import io.redgreen.architecture.mobius.AsyncOp.Companion.idle
import io.redgreen.architecture.mobius.AsyncOp.Companion.inFlight
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.timelapse.core.TrackedFilePath
import java.util.Optional
import java.util.Optional.empty

data class AffectedFilesModel(
  val gitReposAsyncOp: AsyncOp<List<GitRepo>, Nothing>,
  val selectedGitRepo: Optional<GitRepo>,

  val trackedFilesAsyncOp: AsyncOp<List<TrackedFilePath>, Nothing>,
  val selectedFilePath: Optional<TrackedFilePath>,

  val affectingCommitsAsyncOp: AsyncOp<List<AffectingCommit>, Nothing>,
  val selectedCommit: Optional<AffectingCommit>, // TODO This should be an effect!
) {
  companion object {
    val fetchingGitRepos = AffectedFilesModel(
      inFlight(),
      empty(),
      idle(),
      empty(),
      idle(),
      empty(),
    )
  }

  fun gitReposFound(gitRepos: List<GitRepo>): AffectedFilesModel =
    copy(gitReposAsyncOp = content(gitRepos))

  fun gitRepoSelected(gitRepo: GitRepo): AffectedFilesModel = copy(
    selectedGitRepo = Optional.of(gitRepo),
    trackedFilesAsyncOp = inFlight()
  )

  fun trackedFilePathsFetched(
    filePaths: List<TrackedFilePath>
  ): AffectedFilesModel =
    copy(trackedFilesAsyncOp = content(filePaths))

  fun filePathSelected(filePath: TrackedFilePath): AffectedFilesModel = copy(
    selectedFilePath = Optional.of(filePath),
    affectingCommitsAsyncOp = inFlight()
  )

  fun commitsAffectingFilePathFetched(
    affectingCommits: List<AffectingCommit>
  ): AffectedFilesModel =
    copy(affectingCommitsAsyncOp = content(affectingCommits))

  fun affectingCommitSelected(
    affectingCommit: AffectingCommit
  ): AffectedFilesModel =
    copy(selectedCommit = Optional.of(affectingCommit))
}
