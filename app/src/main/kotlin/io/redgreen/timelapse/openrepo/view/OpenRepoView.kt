package io.redgreen.timelapse.openrepo.view

import io.redgreen.timelapse.openrepo.data.RecentRepository

interface OpenRepoView {
  fun displayWelcomeMessage(message: WelcomeMessage)
  fun displayRecentRepositoriesStatus(status: RecentRepositoriesStatus)
  fun displayRecentRepositories(recentRepositories: List<RecentRepository>)
  fun displayFileChooser()
  fun openGitRepository(path: String)
  fun showNotAGitRepositoryError(path: String)
}
