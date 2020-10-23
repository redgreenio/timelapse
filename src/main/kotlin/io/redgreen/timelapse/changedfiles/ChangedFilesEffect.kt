package io.redgreen.timelapse.changedfiles

sealed class ChangedFilesEffect

data class GetChangedFiles(
  val commitId: String,
  val selectedFilePath: String
) : ChangedFilesEffect() {
  sealed class GetChangedFilesFailure {
    object Unknown : GetChangedFilesFailure()
  }
}

data class ShowDiff(
  val commitId: String,
  val filePath: String
) : ChangedFilesEffect()
