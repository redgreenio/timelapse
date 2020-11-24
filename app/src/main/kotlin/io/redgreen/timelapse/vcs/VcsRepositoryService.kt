package io.redgreen.timelapse.vcs

import io.reactivex.rxjava3.core.Single
import java.time.LocalDate

interface VcsRepositoryService {
  fun getChangedFiles(commitId: String): Single<List<ChangedFile>>

  fun getContributions(commitId: String, filePath: String): Single<List<Contribution>>

  fun getFirstCommitOnOrAfter(date: LocalDate): Single<String>

  fun getChangedFilePaths(descendantCommitId: String, ancestorCommitId: String? = null): Single<List<String>>
}
