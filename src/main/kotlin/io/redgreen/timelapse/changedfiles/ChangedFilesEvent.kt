package io.redgreen.timelapse.changedfiles

sealed class ChangedFilesEvent

data class RevisionSelected(
  val commitId: String,
  val filePath: String
) : ChangedFilesEvent()

object NoOtherFilesChanged : ChangedFilesEvent()

data class SomeFilesChanged(
  val filePaths: List<String>
) : ChangedFilesEvent()

object UnableToRetrieveChangedFiles : ChangedFilesEvent()

object RetryRetrievingChangedFiles : ChangedFilesEvent()

data class SelectChangedFile(val index: Int) : ChangedFilesEvent()
