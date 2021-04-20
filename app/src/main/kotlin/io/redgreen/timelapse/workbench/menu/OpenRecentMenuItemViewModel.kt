package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.openrepo.data.RecentGitRepository

sealed class OpenRecentMenuItemViewModel

data class RecentRepositoryMenuItemViewModel(
  val recentRepository: RecentGitRepository,
  val isPresent: Boolean = true,
) : OpenRecentMenuItemViewModel()

object ClearRecentMenuItemViewModel : OpenRecentMenuItemViewModel()

object SeparatorMenuItemViewModel : OpenRecentMenuItemViewModel()
