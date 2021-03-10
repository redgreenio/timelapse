package io.redgreen.timelapse.vcs

import io.reactivex.rxjava3.core.Single
import io.redgreen.timelapse.contentviewer.data.BlobDiffInformation
import io.redgreen.timelapse.domain.BlobDiff
import java.time.LocalDate

interface VcsRepositoryService {
  @Deprecated("Use [GetAffectedFilesUseCase] instead.")
  fun getChangedFiles(commitId: String): Single<List<ChangedFile>>

  fun getContributions(
    commitId: String,
    filePath: String
  ): Single<List<Contribution>>

  fun getFirstCommitOnOrAfter(date: LocalDate): Single<String>

  fun getChangedFilePaths(
    descendantCommitId: String,
    ancestorCommitId: String? = null
  ): Single<List<String>>

  fun getBlobDiff(
    selectedFilePath: String,
    commitId: String
  ): Single<BlobDiff>

  fun getBlobDiffInformation(
    selectedFilePath: String,
    commitId: String
  ): Single<BlobDiffInformation>
}
