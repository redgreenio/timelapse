package toys.affectedfiles.view

import io.redgreen.architecture.mobius.AsyncOp.Content
import io.redgreen.architecture.mobius.view.ViewRenderer
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.timelapse.core.TrackedFilePath
import toys.affectedfiles.model.AffectedFilesModel
import toys.affectedfiles.model.AffectingCommit

class AffectedFilesViewRenderer(
  private val view: AffectedFilesPropsView
) : ViewRenderer<AffectedFilesModel> {
  private var gitReposMemoized: List<GitRepo>? = null
  private var trackedFilePathsMemoized: List<TrackedFilePath>? = null
  private var affectingCommitsMemoized: List<AffectingCommit>? = null

  override fun render(model: AffectedFilesModel) {
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
