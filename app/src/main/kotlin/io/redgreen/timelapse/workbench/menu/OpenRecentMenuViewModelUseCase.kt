package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.EmptyMenu

class OpenRecentMenuViewModelUseCase(
  private val repositoriesStorage: RecentGitRepositoriesStorage
) {
  fun invoke(): OpenRecentMenuViewModel {
    println(repositoriesStorage)
    return EmptyMenu
  }
}
