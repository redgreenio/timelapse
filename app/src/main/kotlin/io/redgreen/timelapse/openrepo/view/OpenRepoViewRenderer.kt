package io.redgreen.timelapse.openrepo.view

import io.redgreen.architecture.mobius.AsyncOp.Content
import io.redgreen.architecture.mobius.AsyncOp.Failure
import io.redgreen.architecture.mobius.AsyncOp.Idle
import io.redgreen.architecture.mobius.AsyncOp.InFlight
import io.redgreen.architecture.mobius.view.ViewRenderer
import io.redgreen.timelapse.openrepo.OpenRepoModel
import io.redgreen.timelapse.openrepo.data.GitUsername
import io.redgreen.timelapse.openrepo.data.RecentRepository
import io.redgreen.timelapse.openrepo.view.RecentRepositoriesStatus.LOADING
import io.redgreen.timelapse.openrepo.view.RecentRepositoriesStatus.NO_RECENT_REPOSITORIES
import io.redgreen.timelapse.openrepo.view.WelcomeMessage.Greeter
import io.redgreen.timelapse.openrepo.view.WelcomeMessage.Stranger

class OpenRepoViewRenderer(
  private val view: OpenRepoView
) : ViewRenderer<OpenRepoModel> {
  override fun render(model: OpenRepoModel) {
    with(view) {
      if (model.gitUsername is GitUsername.NotFound) {
        displayWelcomeMessage(Stranger)
      } else if (model.gitUsername is GitUsername.Found) {
        displayWelcomeMessage(Greeter(model.gitUsername.username))
      }

      when(model.recentRepositoriesAsyncOp.value) {
        Idle, InFlight -> displayRecentRepositoriesStatus(LOADING)
        is Content -> showListOrEmpty(model)
        is Failure -> displayRecentRepositoriesStatus(NO_RECENT_REPOSITORIES)
      }
    }
  }

  private fun OpenRepoView.showListOrEmpty(model: OpenRepoModel) {
    val recentRepositoriesAsyncOp = model.recentRepositoriesAsyncOp
    val recentRepositories = (recentRepositoriesAsyncOp as Content<List<RecentRepository>>).content
    if (recentRepositories.isEmpty()) {
      displayRecentRepositoriesStatus(NO_RECENT_REPOSITORIES)
    } else {
      displayRecentRepositories(recentRepositories)
    }
  }
}
