package io.redgreen.timelapse.applesauce

import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage

class SessionLauncher(
  private val recentGitRepositoriesStorage: RecentGitRepositoriesStorage,
  private val checkIfIsGitRepo: (path: String) -> Boolean = { GitDirectory.from(it).isPresent }
) {
  fun tryRestorePreviousSession(launchWorkbenchAction: (gitRepositoryPath: String) -> Unit) {
    val lastOpenedRepositoryPath = recentGitRepositoriesStorage
      .getLastOpenedRepository()
      .get()
      .path

    if (checkIfIsGitRepo.invoke(lastOpenedRepositoryPath)) {
      launchWorkbenchAction(lastOpenedRepositoryPath)
    }
  }
}
