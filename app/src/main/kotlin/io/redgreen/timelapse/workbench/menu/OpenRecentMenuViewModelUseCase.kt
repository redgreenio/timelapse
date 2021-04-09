package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemViewModel.ClearRecent
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemViewModel.RecentRepository
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.Empty
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.NonEmpty

class OpenRecentMenuViewModelUseCase(
  private val repositoriesStorage: RecentGitRepositoriesStorage
) {
  fun invoke(): OpenRecentMenuViewModel {
    val menuItemViewModels = repositoriesStorage
      .getRecentRepositories()
      .map(RecentGitRepository::path)
      .map(::RecentRepository)

    return if (menuItemViewModels.isEmpty()) {
      Empty
    } else {
      NonEmpty(menuItemViewModels + ClearRecent)
    }
  }
}
