package io.redgreen.timelapse.changedfiles

sealed class ChangedFilesEvent

data class FileAndRevisionSelected(
  val filePath: String,
  val commitId: String
) : ChangedFilesEvent()

object NoOtherFilesChanged : ChangedFilesEvent()

data class SomeMoreFilesChanged(
  val filePaths: List<String>
) : ChangedFilesEvent()

object GettingChangedFilesFailed : ChangedFilesEvent()

object RetryGettingChangedFiles : ChangedFilesEvent()

data class ChangedFileSelected(val index: Int) : ChangedFilesEvent()
