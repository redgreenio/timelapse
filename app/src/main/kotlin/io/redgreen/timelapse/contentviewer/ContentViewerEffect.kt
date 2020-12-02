package io.redgreen.timelapse.contentviewer

sealed class ContentViewerEffect

data class LoadBlobDiffInformation(
  val selectedFilePath: String,
  val commitId: String
) : ContentViewerEffect()

data class LoadBlobDiff(
  val selectedFilePath: String,
  val commitId: String
) : ContentViewerEffect()
