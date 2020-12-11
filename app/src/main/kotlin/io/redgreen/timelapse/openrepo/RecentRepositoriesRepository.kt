package io.redgreen.timelapse.openrepo

import io.redgreen.timelapse.openrepo.data.RecentRepository

interface RecentRepositoriesRepository {
  fun update(recentRepository: RecentRepository)
}
