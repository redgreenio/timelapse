package io.redgreen.timelapse.vcs

import io.reactivex.rxjava3.core.Single

interface VcsRepositoryService {
  fun getChangedFiles(commitId: String): Single<List<ChangedFile>>
  fun getContributions(commitId: String, filePath: String): Single<List<Contribution>>
}
