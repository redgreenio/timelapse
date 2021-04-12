package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.openrepo.data.RecentGitRepository

sealed class OpenRecentMenuItemViewModel

data class RecentRepositoryMenuItemViewModel(
  val repositoryDirectory: String,
  val isPresent: Boolean = true,
  val recentRepository: RecentGitRepository? = null,
) : OpenRecentMenuItemViewModel()

object ClearRecentMenuItemViewModel : OpenRecentMenuItemViewModel()

object SeparatorMenuItemViewModel : OpenRecentMenuItemViewModel()
