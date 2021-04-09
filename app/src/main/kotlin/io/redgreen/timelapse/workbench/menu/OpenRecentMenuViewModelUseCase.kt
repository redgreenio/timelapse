package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.EmptyMenu
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.NonEmptyMenu

class OpenRecentMenuViewModelUseCase(
  private val repositoriesStorage: RecentGitRepositoriesStorage
) {
  fun invoke(): OpenRecentMenuViewModel {
    val recentRepositories = repositoriesStorage.getRecentRepositories()
    return if (recentRepositories.isEmpty()) {
      EmptyMenu
    } else {
      val menuItemViewModels = recentRepositories
        .map { NonEmptyMenu.RecentRepositoryMenuItemViewModel(it.path) }
      NonEmptyMenu(menuItemViewModels + NonEmptyMenu.ClearRecentsMenuItemViewModel)
    }
  }
}
