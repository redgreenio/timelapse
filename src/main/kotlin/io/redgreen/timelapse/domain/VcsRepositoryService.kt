package io.redgreen.timelapse.domain

import io.reactivex.rxjava3.core.Single
import io.redgreen.timelapse.vcs.FileChange

interface VcsRepositoryService {
  fun getFileChanges(commitId: String): Single<List<FileChange>>
}
