package io.redgreen.timelapse.workbench.menu

sealed class OpenRecentMenuViewModel

object EmptyMenuViewModel : OpenRecentMenuViewModel()

data class NonEmptyMenuViewModel(
  val menuItemViewModels: List<OpenRecentMenuItemViewModel>
) : OpenRecentMenuViewModel()
