package io.redgreen.timelapse.openrepo.view

import io.redgreen.timelapse.mobius.AsyncOp.Content
import io.redgreen.timelapse.mobius.AsyncOp.Failure
import io.redgreen.timelapse.mobius.AsyncOp.Idle
import io.redgreen.timelapse.mobius.AsyncOp.InFlight
import io.redgreen.timelapse.mobius.view.ViewRenderer
import io.redgreen.timelapse.openrepo.OpenRepoModel
import io.redgreen.timelapse.openrepo.data.GitUsername
import io.redgreen.timelapse.openrepo.data.RecentRepository
import io.redgreen.timelapse.openrepo.view.RecentRepositoriesStatus.LOADING
import io.redgreen.timelapse.openrepo.view.RecentRepositoriesStatus.NO_RECENT_REPOSITORIES
import io.redgreen.timelapse.openrepo.view.WelcomeMessage.Stranger

class OpenRepoViewRenderer(
  private val view: OpenRepoView
) : ViewRenderer<OpenRepoModel> {
  override fun render(model: OpenRepoModel) {
    with(view) {
      if (model.gitUsername is GitUsername.NotFound) {
        displayWelcomeMessage(Stranger)
      } else if (model.gitUsername is GitUsername.Found) {
        displayWelcomeMessage(WelcomeMessage.Greeter(model.gitUsername.username))
      }

      when(model.recentRepositoriesAsyncOp.value) {
        Idle, InFlight -> displayRecentRepositoriesStatus(LOADING)
        is Content -> {
          val recentRepositories = (model.recentRepositoriesAsyncOp.value as Content<List<RecentRepository>>).content
          if (recentRepositories.size == 0) {
            displayRecentRepositoriesStatus(NO_RECENT_REPOSITORIES)
          } else {
            displayRecentRepositories(recentRepositories)
          }
        }
        is Failure -> displayRecentRepositoriesStatus(NO_RECENT_REPOSITORIES)
      }
    }
  }
}
