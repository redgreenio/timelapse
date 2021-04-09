package io.redgreen.timelapse.workbench.menu

sealed class OpenRecentMenuViewModel {
  object Empty : OpenRecentMenuViewModel()

  data class NonEmpty(
    val menuItemViewModels: List<OpenRecentMenuItemViewModel>
  ) : OpenRecentMenuViewModel()
}

sealed class OpenRecentMenuItemViewModel {
  data class RecentRepository(
    val repositoryDirectory: String,
    val isPresent: Boolean = true
  ) : OpenRecentMenuItemViewModel()

  object ClearRecent : OpenRecentMenuItemViewModel()

  object Separator : OpenRecentMenuItemViewModel()
}
