package io.redgreen.timelapse.changedfiles

sealed class ChangedFilesEvent

data class RevisionSelected(
  val commitId: String,
  val filePath: String
) : ChangedFilesEvent()
