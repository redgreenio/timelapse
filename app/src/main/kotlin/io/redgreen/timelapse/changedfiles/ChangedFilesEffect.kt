package io.redgreen.timelapse.changedfiles

import io.redgreen.timelapse.vcs.ChangedFile

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
  val filePath: ChangedFile
) : ChangedFilesEffect()
