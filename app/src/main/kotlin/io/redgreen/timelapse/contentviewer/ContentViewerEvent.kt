package io.redgreen.timelapse.contentviewer

import io.redgreen.timelapse.domain.BlobDiff
import io.redgreen.timelapse.mobius.annotations.EffectEvent
import io.redgreen.timelapse.mobius.annotations.UiEvent

sealed class ContentViewerEvent

@UiEvent
data class FileAndRevisionSelected(
  val selectedFilePath: String,
  val commitId: String
) : ContentViewerEvent()

@EffectEvent(LoadBlobDiffInformation::class)
data class BlobDiffInformationLoaded(
  val blobDiffInformation: BlobDiffInformation
) : ContentViewerEvent()

@EffectEvent(LoadBlobDiffInformation::class)
object UnableToLoadBlobDiffInformation : ContentViewerEvent()

@EffectEvent(LoadBlobDiff::class)
data class BlobDiffLoaded(
  val blobDiff: BlobDiff
) : ContentViewerEvent()

@EffectEvent(LoadBlobDiff::class)
object UnableToLoadBlobDiff : ContentViewerEvent()

@UiEvent
object Retry : ContentViewerEvent()

@UiEvent
object CommitIdClicked : ContentViewerEvent()
