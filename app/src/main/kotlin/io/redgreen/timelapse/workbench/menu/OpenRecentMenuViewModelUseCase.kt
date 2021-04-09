package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemViewModel.ClearRecent
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemViewModel.RecentRepository
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.Empty
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.NonEmpty

class OpenRecentMenuViewModelUseCase(
  private val repositoriesStorage: RecentGitRepositoriesStorage,
  private val checkIfIsGitRepo: (path: String) -> Boolean = { GitDirectory.from(it).isPresent }
) {
  fun invoke(): OpenRecentMenuViewModel {
    val menuItemViewModels = repositoriesStorage
      .getRecentRepositories()
      .map(RecentGitRepository::path)
      .map { RecentRepository(it, checkIfIsGitRepo(it)) }

    return if (menuItemViewModels.isEmpty()) {
      Empty
    } else {
      NonEmpty(menuItemViewModels + ClearRecent)
    }
  }
}
