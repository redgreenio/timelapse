package io.redgreen.timelapse.router

import io.redgreen.timelapse.foo.IS_GIT_REPOSITORY_PREDICATE
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.router.Destination.WORKBENCH

class SessionLauncher(
  private val recentGitRepositoriesStorage: RecentGitRepositoriesStorage,
  private val isGitRepositoryPredicate: (path: String) -> Boolean = IS_GIT_REPOSITORY_PREDICATE
) {
  fun tryRestorePreviousSession(
    launchWorkbenchAction: (gitRepositoryPath: String) -> Unit,
    launchWelcomeScreenAction: () -> Unit
  ) {
    if (canLaunchWorkbench(lastGitRepositoryPath())) {
      launchWorkbenchAction(lastGitRepositoryPath()!!)
    } else {
      launchWelcomeScreenAction()
    }
  }

  private fun canLaunchWorkbench(lastGitRepositoryPath: String?): Boolean {
    return recentGitRepositoriesStorage.getSessionExitDestination() == WORKBENCH &&
      lastGitRepositoryPath != null &&
      isGitRepositoryPredicate(lastGitRepositoryPath)
  }

  private fun lastGitRepositoryPath(): String? {
    return recentGitRepositoriesStorage
      .getLastOpenedRepository()
      .orElseGet { null }
      ?.path
  }
}
