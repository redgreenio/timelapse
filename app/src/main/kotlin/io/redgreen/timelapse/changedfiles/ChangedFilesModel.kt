package io.redgreen.timelapse.changedfiles

import io.redgreen.timelapse.changedfiles.GetChangedFiles.Failure
import io.redgreen.timelapse.changedfiles.GetChangedFiles.Failure.Unknown
import io.redgreen.timelapse.mobius.AsyncOp
import io.redgreen.timelapse.mobius.AsyncOp.Companion.content
import io.redgreen.timelapse.mobius.AsyncOp.Companion.failure
import io.redgreen.timelapse.mobius.AsyncOp.Companion.idle
import io.redgreen.timelapse.mobius.AsyncOp.Companion.inFlight
import io.redgreen.timelapse.vcs.ChangedFile

data class ChangedFilesModel(
  val commitId: String?,
  val selectedFilePath: String?,
  val getChangedFilesAsyncOp: AsyncOp<List<ChangedFile>, Failure> = idle(),
) {
  companion object {
    fun noFileAndRevisionSelected(): ChangedFilesModel =
      ChangedFilesModel(commitId = null, selectedFilePath = null)
  }

  fun fileAndRevisionSelected(
    selectedFilePath: String,
    commitId: String,
  ): ChangedFilesModel =
    copy(commitId = commitId, selectedFilePath = selectedFilePath, getChangedFilesAsyncOp = inFlight())

  fun noOtherFilesChanged(): ChangedFilesModel =
    copy(getChangedFilesAsyncOp = content(emptyList()))

  fun someMoreFilesChanged(changedFiles: List<ChangedFile>): ChangedFilesModel =
    copy(getChangedFilesAsyncOp = content(changedFiles.filter { it.filePath != selectedFilePath }))

  fun gettingChangedFilesFailed(): ChangedFilesModel =
    copy(getChangedFilesAsyncOp = failure(Unknown))

  fun retryGettingChangedFiles(): ChangedFilesModel =
    copy(getChangedFilesAsyncOp = inFlight())
}
