package liftoff.affectedfiles.props.view

import com.spotify.diffuser.diffuser.Diffuser
import com.spotify.diffuser.diffuser.Diffuser.into
import com.spotify.diffuser.diffuser.Diffuser.intoAlways
import com.spotify.diffuser.diffuser.Diffuser.map
import io.redgreen.architecture.mobius.AsyncOp
import io.redgreen.architecture.mobius.AsyncOp.Content
import io.redgreen.architecture.mobius.view.ViewRenderer
import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.git.model.TrackedFilePath
import javafx.application.Platform
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
    Platform.runLater { diffuser.run(model) }
  }

  private fun handleGitReposAsyncOp(
    asyncOp: AsyncOp<List<GitDirectory>, Nothing>
  ) {
    if (asyncOp is Content) {
      view.populateGitDirectories(asyncOp.content)
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
    } else {
      view.clearAffectingCommits()
    }
  }
}
