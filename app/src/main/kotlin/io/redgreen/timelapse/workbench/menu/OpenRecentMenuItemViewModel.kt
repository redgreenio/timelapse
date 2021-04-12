package io.redgreen.timelapse.workbench.menu

sealed class OpenRecentMenuItemViewModel

data class RecentRepository(
  val repositoryDirectory: String,
  val isPresent: Boolean = true
) : OpenRecentMenuItemViewModel()

object ClearRecentMenuItemViewModel : OpenRecentMenuItemViewModel()

object SeparatorMenuItemViewModel : OpenRecentMenuItemViewModel()
