package io.redgreen.timelapse.openrepo.storage

import io.redgreen.timelapse.openrepo.data.RecentGitRepository

interface RecentGitRepositoriesStorage {
  fun update(recentGitRepository: RecentGitRepository)
  fun getRecentRepositories(): List<RecentGitRepository>
}
