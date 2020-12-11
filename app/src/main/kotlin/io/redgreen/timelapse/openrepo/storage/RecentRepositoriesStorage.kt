package io.redgreen.timelapse.openrepo.storage

import io.redgreen.timelapse.openrepo.data.RecentRepository

interface RecentRepositoriesStorage {
  fun update(recentRepository: RecentRepository)
  fun getRecentRepositories(): List<RecentRepository>
}
