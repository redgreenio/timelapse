package io.redgreen.timelapse.openrepo.view

import io.redgreen.timelapse.openrepo.data.RecentRepository

interface OpenRepoView {
  fun displayFileChooser()
  fun openGitRepository(path: String)
  fun showNotAGitRepositoryError(path: String)
  fun displayWelcomeMessage(message: WelcomeMessage)
  fun displayRecentRepositoriesStatus(status: RecentRepositoriesStatus)
  fun hideRecentRepositoriesState()
  fun displayRecentRepositories(recentRepositories: List<RecentRepository>)
}
