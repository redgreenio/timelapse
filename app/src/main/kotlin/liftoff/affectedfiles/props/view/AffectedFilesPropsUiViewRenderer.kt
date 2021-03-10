package liftoff.affectedfiles.props.view

import com.spotify.diffuser.diffuser.Diffuser
import com.spotify.diffuser.diffuser.Diffuser.into
import com.spotify.diffuser.diffuser.Diffuser.intoAlways
import com.spotify.diffuser.diffuser.Diffuser.map
import io.redgreen.architecture.mobius.AsyncOp
import io.redgreen.architecture.mobius.AsyncOp.Content
import io.redgreen.architecture.mobius.view.ViewRenderer
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.timelapse.core.TrackedFilePath
import liftoff.affectedfiles.model.AffectingCommit
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiModel

class AffectedFilesPropsUiViewRenderer(
  private val view: AffectedFilesPropsUiView
) : ViewRenderer<AffectedFilesPropsUiModel> {
  private val diffuser = Diffuser(
    map(AffectedFilesPropsUiModel::gitReposAsyncOp, into(::handleGitReposAsyncOp)),
    map(AffectedFilesPropsUiModel::trackedFilesAsyncOp, into(::handleTrackedFilesAsyncOp)),
    map(AffectedFilesPropsUiModel::affectingCommitsAsyncOp, into(::handleAffectingCommitsAsyncOp)),
    intoAlways { view.clearAffectedFileCallbackText() }
  )

  override fun render(model: AffectedFilesPropsUiModel) {
    diffuser.run(model)
  }

  private fun handleGitReposAsyncOp(
    asyncOp: AsyncOp<List<GitRepo>, Nothing>
  ) {
    if (asyncOp is Content) {
      view.populateGitRepos(asyncOp.content)
    } else {
      with(view) {
        clearTrackedFiles()
        clearAffectingCommits()
      }
    }
  }

  private fun handleTrackedFilesAsyncOp(
    asyncOp: AsyncOp<List<TrackedFilePath>, Nothing>
  ) {
    if (asyncOp is Content) {
      view.populateTrackedFiles(asyncOp.content)
    } else {
      view.clearAffectingCommits()
    }
  }

  private fun handleAffectingCommitsAsyncOp(
    asyncOp: AsyncOp<List<AffectingCommit>, Nothing>
  ) {
    if (asyncOp is Content) {
      view.populateAffectingCommits(asyncOp.content)
    }
  }
}
