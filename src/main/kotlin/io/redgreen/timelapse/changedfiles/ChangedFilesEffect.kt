package io.redgreen.timelapse.changedfiles

sealed class ChangedFilesEffect

data class FetchChangedFiles(
  val commitId: String,
  val selectedFilePath: String
) : ChangedFilesEffect()
