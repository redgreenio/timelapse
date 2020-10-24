package io.redgreen.timelapse.changedfiles

import io.redgreen.timelapse.vcs.FileChange

sealed class ChangedFilesEvent

data class FileAndRevisionSelected(
  val filePath: String,
  val commitId: String
) : ChangedFilesEvent()

object NoOtherFilesChanged : ChangedFilesEvent()

data class SomeMoreFilesChanged(
  @Deprecated("Use `fileChanges` instead.")
  val filePaths: ChangedFiles = emptyList(),
  val fileChanges: List<FileChange> = emptyList()
) : ChangedFilesEvent()

object GettingChangedFilesFailed : ChangedFilesEvent()

object RetryGettingChangedFiles : ChangedFilesEvent()

data class ChangedFileSelected(val index: Int) : ChangedFilesEvent()
