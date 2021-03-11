package liftoff.affectedfiles.props.mobius

import io.redgreen.architecture.mobius.AsyncOp
import io.redgreen.architecture.mobius.AsyncOp.Companion.content
import io.redgreen.architecture.mobius.AsyncOp.Companion.idle
import io.redgreen.architecture.mobius.AsyncOp.Companion.inFlight
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.timelapse.git.model.TrackedFilePath
import java.util.Optional
import java.util.Optional.empty
import liftoff.affectedfiles.model.AffectingCommit

data class AffectedFilesPropsUiModel(
  val gitReposAsyncOp: AsyncOp<List<GitRepo>, Nothing>,
  val selectedGitRepo: Optional<GitRepo>,

  val trackedFilesAsyncOp: AsyncOp<List<TrackedFilePath>, Nothing>,
  val selectedFilePath: Optional<TrackedFilePath>,

  val affectingCommitsAsyncOp: AsyncOp<List<AffectingCommit>, Nothing>,
  val selectedCommit: Optional<AffectingCommit>, // TODO This should be an effect!
) {
  companion object {
    val fetchingGitRepos = AffectedFilesPropsUiModel(
      inFlight(),
      empty(),
      idle(),
      empty(),
      idle(),
      empty(),
    )
  }

  fun gitReposFound(gitRepos: List<GitRepo>): AffectedFilesPropsUiModel =
    copy(gitReposAsyncOp = content(gitRepos))

  fun gitRepoSelected(gitRepo: GitRepo): AffectedFilesPropsUiModel = copy(
    selectedGitRepo = Optional.of(gitRepo),
    trackedFilesAsyncOp = inFlight()
  )

  fun trackedFilePathsFetched(
    filePaths: List<TrackedFilePath>
  ): AffectedFilesPropsUiModel =
    copy(trackedFilesAsyncOp = content(filePaths))

  fun filePathSelected(filePath: TrackedFilePath): AffectedFilesPropsUiModel = copy(
    selectedFilePath = Optional.of(filePath),
    affectingCommitsAsyncOp = inFlight()
  )

  fun commitsAffectingFilePathFetched(
    affectingCommits: List<AffectingCommit>
  ): AffectedFilesPropsUiModel =
    copy(affectingCommitsAsyncOp = content(affectingCommits))

  fun affectingCommitSelected(
    affectingCommit: AffectingCommit
  ): AffectedFilesPropsUiModel =
    copy(selectedCommit = Optional.of(affectingCommit))
}
