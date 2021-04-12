package io.redgreen.timelapse.workbench.menu

sealed class OpenRecentMenuItemViewModel

data class RecentRepositoryMenuItemViewModel(
  val repositoryDirectory: String,
  val isPresent: Boolean = true
) : OpenRecentMenuItemViewModel()

object ClearRecentMenuItemViewModel : OpenRecentMenuItemViewModel()

object SeparatorMenuItemViewModel : OpenRecentMenuItemViewModel()
