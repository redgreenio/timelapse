package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.foo.IS_GIT_REPOSITORY_PREDICATE
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemViewModel.RecentRepository
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.Empty
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.NonEmpty

class OpenRecentMenuViewModelUseCase(
  private val repositoriesStorage: RecentGitRepositoriesStorage,
  private val isGitRepositoryPredicate: (path: String) -> Boolean = IS_GIT_REPOSITORY_PREDICATE
) {
  fun invoke(): OpenRecentMenuViewModel {
    val menuItemViewModels = repositoriesStorage
      .getRecentRepositories()
      .map(RecentGitRepository::path)
      .map { RecentRepository(it, isGitRepositoryPredicate(it)) }

    return if (menuItemViewModels.isEmpty()) {
      Empty
    } else {
      NonEmpty(menuItemViewModels + Separator + ClearRecent)
    }
  }
}
