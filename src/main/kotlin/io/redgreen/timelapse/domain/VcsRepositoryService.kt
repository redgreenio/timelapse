package io.redgreen.timelapse.domain

import io.reactivex.rxjava3.core.Single
import io.redgreen.timelapse.vcs.ChangedFile

interface VcsRepositoryService {
  fun getChangedFiles(commitId: String): Single<List<ChangedFile>>
}
