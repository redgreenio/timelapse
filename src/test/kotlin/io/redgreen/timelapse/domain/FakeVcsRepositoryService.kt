package io.redgreen.timelapse.domain

import io.reactivex.rxjava3.core.Single
import io.redgreen.timelapse.changedfiles.ChangedFiles

open class FakeVcsRepositoryService : VcsRepositoryService {
  override fun getChangedFilePaths(commitId: String): Single<ChangedFiles> {
    throw UnsupportedOperationException("Stub! override for desired behavior :)")
  }
}
