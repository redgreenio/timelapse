package io.redgreen.timelapse.workbench.menu

sealed class OpenRecentMenuViewModel {
  object EmptyMenu : OpenRecentMenuViewModel()
  data class NonEmptyMenu(val contents: List<Any>) : OpenRecentMenuViewModel() {
    data class RecentRepositoryMenuItemViewModel(val gitProjectDirectory: String)
    object ClearRecentsMenuItemViewModel
  }
}
