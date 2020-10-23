package io.redgreen.timelapse.domain

import io.reactivex.rxjava3.core.Single
import io.redgreen.timelapse.changedfiles.ChangedFiles

interface VcsRepositoryService {
  fun getChangedFilePaths(commitId: String): Single<ChangedFiles>
}
