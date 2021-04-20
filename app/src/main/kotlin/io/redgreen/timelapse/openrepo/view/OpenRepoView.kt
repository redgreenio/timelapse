package io.redgreen.timelapse.openrepo.view

import io.redgreen.timelapse.openrepo.data.RecentGitRepository

interface OpenRepoView {
  fun displayWelcomeMessage(message: WelcomeMessage)
  fun displayRecentRepositoriesStatus(status: RecentRepositoriesStatus)
  fun displayRecentRepositories(recentGitRepositories: List<RecentGitRepository>)
  fun displayFileChooser()
  fun openGitRepository(path: String)
  fun showNotAGitRepositoryError(path: String)
  fun closeWelcomeStage()
}
