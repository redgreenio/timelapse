package io.redgreen.timelapse.changedfiles

import io.redgreen.timelapse.changedfiles.GetChangedFiles.GetChangedFilesFailure
import io.redgreen.timelapse.changedfiles.GetChangedFiles.GetChangedFilesFailure.Unknown
import io.redgreen.timelapse.mobius.AsyncOp
import io.redgreen.timelapse.mobius.AsyncOp.Companion.content
import io.redgreen.timelapse.mobius.AsyncOp.Companion.failure
import io.redgreen.timelapse.mobius.AsyncOp.Companion.idle
import io.redgreen.timelapse.mobius.AsyncOp.Companion.inFlight

typealias ChangedFiles = List<String>

data class ChangedFilesModel(
  val commitId: String?,
  val filePath: String?,
  val getChangedFilesAsyncOp: AsyncOp<ChangedFiles, GetChangedFilesFailure> = idle(),
) {
  companion object {
    fun noFileAndRevisionSelected(): ChangedFilesModel =
      ChangedFilesModel(commitId = null, filePath = null)
  }

  fun fileAndRevisionSelected(
    filePath: String,
    commitId: String,
  ): ChangedFilesModel =
    copy(commitId = commitId, filePath = filePath, getChangedFilesAsyncOp = inFlight())

  fun noOtherFilesChanged(): ChangedFilesModel =
    copy(getChangedFilesAsyncOp = content(emptyList()))

  fun someMoreFilesChanged(filePaths: ChangedFiles): ChangedFilesModel =
    copy(getChangedFilesAsyncOp = content(filePaths))

  fun gettingChangedFilesFailed(): ChangedFilesModel =
    copy(getChangedFilesAsyncOp = failure(Unknown))

  fun retryGettingChangedFiles(): ChangedFilesModel =
    copy(getChangedFilesAsyncOp = inFlight())
}
