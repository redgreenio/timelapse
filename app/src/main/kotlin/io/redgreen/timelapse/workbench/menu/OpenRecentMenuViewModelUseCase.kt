package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.foo.IS_GIT_REPOSITORY_PREDICATE
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage

class OpenRecentMenuViewModelUseCase(
  private val repositoriesStorage: RecentGitRepositoriesStorage,
  private val isGitRepositoryPredicate: (path: String) -> Boolean = IS_GIT_REPOSITORY_PREDICATE
) {
  fun invoke(): OpenRecentMenuViewModel {
    val menuItemViewModels = repositoriesStorage
      .getRecentRepositories()
      .map { RecentRepositoryMenuItemViewModel(it, isGitRepositoryPredicate(it.path)) }

    return if (menuItemViewModels.isEmpty()) {
      Empty
    } else {
      NonEmpty(menuItemViewModels + SeparatorMenuItemViewModel + ClearRecentMenuItemViewModel)
    }
  }
}
