package io.redgreen.timelapse.changedfiles

import io.redgreen.timelapse.vcs.ChangedFile

sealed class ChangedFilesEvent

data class FileAndRevisionSelected(
  val filePath: String,
  val commitId: String
) : ChangedFilesEvent()

object NoOtherFilesChanged : ChangedFilesEvent()

data class SomeMoreFilesChanged(
  val changedFiles: List<ChangedFile>
) : ChangedFilesEvent()

object GettingChangedFilesFailed : ChangedFilesEvent()

object RetryGettingChangedFiles : ChangedFilesEvent()

data class ChangedFileSelected(val index: Int) : ChangedFilesEvent()
