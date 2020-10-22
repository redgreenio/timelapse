package io.redgreen.timelapse.changedfiles

sealed class ChangedFiles {
  object Retrieving : ChangedFiles()
  object NoOtherFilesChanged : ChangedFiles()
  data class FilesChanged(val filePaths: List<String>) : ChangedFiles()
  object ErrorRetrievingChangedFiles : ChangedFiles()
}
