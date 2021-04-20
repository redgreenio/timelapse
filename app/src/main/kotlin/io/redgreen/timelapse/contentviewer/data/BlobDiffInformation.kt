package io.redgreen.timelapse.contentviewer.data

data class BlobDiffInformation(
  val selectedFilePath: String,
  val commitId: String,
  val message: String,
  val deletions: Int,
  val insertions: Int,
  val changedFiles: Int
)
