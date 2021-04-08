package io.redgreen.timelapse.router

import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.router.Destination.WORKBENCH

class SessionLauncher(
  private val recentGitRepositoriesStorage: RecentGitRepositoriesStorage,
  private val checkIfIsGitRepo: (path: String) -> Boolean = { GitDirectory.from(it).isPresent }
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
      checkIfIsGitRepo(lastGitRepositoryPath)
  }

  private fun lastGitRepositoryPath(): String? {
    return recentGitRepositoriesStorage
      .getLastOpenedRepository()
      .orElseGet { null }
      ?.path
  }
}
