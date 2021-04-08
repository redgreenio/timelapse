package io.redgreen.timelapse.openrepo.storage

import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.router.Destination
import java.util.Optional

interface RecentGitRepositoriesStorage {
  fun update(recentGitRepository: RecentGitRepository)
  fun getRecentRepositories(): List<RecentGitRepository>
  fun getLastOpenedRepository(): Optional<RecentGitRepository>
  fun setQuitDestination(welcomeScreen: Destination)
  fun getQuitDestination(): Destination
}
