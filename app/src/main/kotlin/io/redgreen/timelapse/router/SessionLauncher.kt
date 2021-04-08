package io.redgreen.timelapse.router

import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.router.Destination.WELCOME_SCREEN

class SessionLauncher(
  private val recentGitRepositoriesStorage: RecentGitRepositoriesStorage,
  private val checkIfIsGitRepo: (path: String) -> Boolean = { GitDirectory.from(it).isPresent }
) {
  fun tryRestorePreviousSession(
    launchWorkbenchAction: (gitRepositoryPath: String) -> Unit,
    launchWelcomeScreenAction: () -> Unit
  ) {
    if (recentGitRepositoriesStorage.getSessionExitDestination() == WELCOME_SCREEN) {
      launchWelcomeScreenAction()
      return
    }

    if (recentGitRepositoriesStorage.getLastOpenedRepository().isEmpty) {
      launchWelcomeScreenAction()
      return
    }

    val lastOpenedRepositoryPath = recentGitRepositoriesStorage
      .getLastOpenedRepository()
      .get()
      .path

    if (checkIfIsGitRepo(lastOpenedRepositoryPath)) {
      launchWorkbenchAction(lastOpenedRepositoryPath)
    } else {
      launchWelcomeScreenAction()
    }
  }
}
