package io.redgreen.timelapse.contentviewer

sealed class ContentViewerEffect

data class LoadBlobDiffInformation(
  val selectedFilePath: String,
  val commitId: String
) : ContentViewerEffect() {
  sealed class Failure {
    object Unknown : Failure()
  }
}

data class LoadBlobDiff(
  val selectedFilePath: String,
  val commitId: String
) : ContentViewerEffect() {
  sealed class Failure {
    object Unknown : Failure()
  }
}
