package io.redgreen.timelapse.changedfiles

import io.redgreen.timelapse.changedfiles.GetChangedFiles.GetChangedFilesFailure
import io.redgreen.timelapse.changedfiles.GetChangedFiles.GetChangedFilesFailure.Unknown
import io.redgreen.timelapse.mobius.AsyncOp
import io.redgreen.timelapse.mobius.AsyncOp.Companion.content
import io.redgreen.timelapse.mobius.AsyncOp.Companion.failure
import io.redgreen.timelapse.mobius.AsyncOp.Companion.idle
import io.redgreen.timelapse.mobius.AsyncOp.Companion.inFlight
import io.redgreen.timelapse.vcs.ChangedFile

data class ChangedFilesModel(
  val commitId: String?,
  val filePath: String?,
  val getChangedFilesAsyncOp: AsyncOp<List<ChangedFile>, GetChangedFilesFailure> = idle(),
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

  fun someMoreFilesChanged(changedFiles: List<ChangedFile>): ChangedFilesModel =
    copy(getChangedFilesAsyncOp = content(changedFiles))

  fun gettingChangedFilesFailed(): ChangedFilesModel =
    copy(getChangedFilesAsyncOp = failure(Unknown))

  fun retryGettingChangedFiles(): ChangedFilesModel =
    copy(getChangedFilesAsyncOp = inFlight())
}
