package io.redgreen.timelapse.workbench.menu

sealed class OpenRecentMenuItemViewModel {
  data class RecentRepository(
    val repositoryDirectory: String,
    val isPresent: Boolean = true
  ) : OpenRecentMenuItemViewModel()

  object ClearRecent : OpenRecentMenuItemViewModel()

  object Separator : OpenRecentMenuItemViewModel()
}
