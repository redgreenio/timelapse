package io.redgreen.timelapse.changedfiles

sealed class ChangedFilesEffect

data class FetchChangedFiles(
  val commitId: String,
  val selectedFilePath: String
) : ChangedFilesEffect()

data class ChangedFileSelected(
  val commitId: String,
  val filePath: String
) : ChangedFilesEffect()
