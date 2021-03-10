package liftoff.affectedfiles.props.view

import io.redgreen.architecture.mobius.AsyncOp.Content
import io.redgreen.architecture.mobius.view.ViewRenderer
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.timelapse.core.TrackedFilePath
import liftoff.affectedfiles.model.AffectingCommit
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiModel

class AffectedFilesPropsUiViewRenderer(
  private val view: AffectedFilesPropsUiView
) : ViewRenderer<AffectedFilesPropsUiModel> {
  private var gitReposMemoized: List<GitRepo>? = null
  private var trackedFilePathsMemoized: List<TrackedFilePath>? = null
  private var affectingCommitsMemoized: List<AffectingCommit>? = null

  override fun render(model: AffectedFilesPropsUiModel) {
    if (model.gitReposAsyncOp is Content && gitReposMemoized != model.gitReposAsyncOp.content) {
      gitReposMemoized = model.gitReposAsyncOp.content

      with(view) {
        populateGitRepos(gitReposMemoized!!)
        populateTrackedFiles(emptyList())
        populateAffectingCommits(emptyList())
      }
    }

    if (model.trackedFilesAsyncOp is Content && trackedFilePathsMemoized != model.trackedFilesAsyncOp.content) {
      trackedFilePathsMemoized = model.trackedFilesAsyncOp.content
      with(view) {
        populateTrackedFiles(trackedFilePathsMemoized!!)
        populateAffectingCommits(emptyList())
      }
    }

    if (model.affectingCommitsAsyncOp is Content && affectingCommitsMemoized != model.affectingCommitsAsyncOp.content
    ) {
      affectingCommitsMemoized = model.affectingCommitsAsyncOp.content
      view.populateAffectingCommits(affectingCommitsMemoized!!)
    }

    view.clearAffectedFileCallbackText()
  }
}
