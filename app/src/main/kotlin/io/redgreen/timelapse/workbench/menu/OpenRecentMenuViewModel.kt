package io.redgreen.timelapse.workbench.menu

sealed class OpenRecentMenuViewModel {
  object Empty : OpenRecentMenuViewModel()
}

data class NonEmpty(
  val menuItemViewModels: List<OpenRecentMenuItemViewModel>
) : OpenRecentMenuViewModel()
