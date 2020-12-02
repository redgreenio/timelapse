package io.redgreen.timelapse.contentviewer

import io.redgreen.timelapse.mobius.annotations.UiEvent

sealed class ContentViewerEvent

@UiEvent
data class FileAndRevisionSelected(
  val selectedFilePath: String,
  val commitId: String
) : ContentViewerEvent()
