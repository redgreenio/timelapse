package io.redgreen.timelapse.openrepo.storage

import io.redgreen.timelapse.openrepo.data.RecentGitRepository

interface RecentRepositoriesStorage {
  fun update(recentGitRepository: RecentGitRepository)
  fun getRecentRepositories(): List<RecentGitRepository>
}
