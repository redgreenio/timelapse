package io.redgreen.timelapse.changedfiles

import io.redgreen.timelapse.changedfiles.ChangedFiles.ErrorRetrievingChangedFiles
import io.redgreen.timelapse.changedfiles.ChangedFiles.FilesChanged
import io.redgreen.timelapse.changedfiles.ChangedFiles.NoOtherFilesChanged
import io.redgreen.timelapse.changedfiles.ChangedFiles.Retrieving

sealed class ChangedFilesModel {
  object NoSelection : ChangedFilesModel() {
    fun revisionSelected(
      commitId: String,
      filePath: String,
    ): ChangedFilesModel =
      HasSelection(commitId, filePath, Retrieving)
  }

  data class HasSelection(
    val commitId: String,
    val filePath: String,
    val changedFiles: ChangedFiles,
  ) : ChangedFilesModel() {
    fun noOtherFilesChanged(): HasSelection =
      copy(changedFiles = NoOtherFilesChanged)

    fun someFilesChanged(filePaths: List<String>): HasSelection =
      copy(changedFiles = FilesChanged(filePaths))

    fun unableToRetrieveChangedFiles(): HasSelection =
      copy(changedFiles = ErrorRetrievingChangedFiles)

    fun retryRetrievingChangedFiles(): HasSelection =
      copy(changedFiles = Retrieving)
  }
}
