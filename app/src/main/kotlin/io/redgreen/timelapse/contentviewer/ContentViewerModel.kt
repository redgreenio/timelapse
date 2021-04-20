package io.redgreen.timelapse.contentviewer

import io.redgreen.architecture.mobius.AsyncOp
import io.redgreen.architecture.mobius.AsyncOp.Companion.content
import io.redgreen.architecture.mobius.AsyncOp.Companion.failure
import io.redgreen.architecture.mobius.AsyncOp.Companion.idle
import io.redgreen.architecture.mobius.AsyncOp.Companion.inFlight
import io.redgreen.timelapse.contentviewer.data.BlobDiffInformation
import io.redgreen.timelapse.domain.BlobDiff

data class ContentViewerModel(
  val selectedFilePath: String?,
  val commitId: String?,
  val loadBlobDiffInformationAsyncOp: AsyncOp<BlobDiffInformation, Any> = idle(),
  val loadBlobDiffAsyncOp: AsyncOp<BlobDiff, Any> = idle()
) {
  companion object {
    fun noFileAndRevisionSelected(): ContentViewerModel {
      return ContentViewerModel(null, null)
    }
  }

  fun fileAndRevisionSelected(
    selectedFilePath: String,
    commitId: String
  ): ContentViewerModel {
    return copy(
      selectedFilePath = selectedFilePath,
      commitId = commitId,
      loadBlobDiffInformationAsyncOp = inFlight(),
      loadBlobDiffAsyncOp = inFlight()
    )
  }

  fun blobDiffInformationLoaded(
    blobDiffInformation: BlobDiffInformation
  ): ContentViewerModel {
    return copy(
      loadBlobDiffInformationAsyncOp = content(blobDiffInformation)
    )
  }

  fun blobDiffLoaded(blobDiff: BlobDiff): ContentViewerModel {
    return copy(
      loadBlobDiffAsyncOp = content(blobDiff)
    )
  }

  fun unableToLoadBlobDiffInformation(): ContentViewerModel {
    return copy(
      loadBlobDiffInformationAsyncOp = failure(LoadBlobDiffInformation.Failure.Unknown)
    )
  }

  fun unableToLoadBlobDiff(): ContentViewerModel {
    return copy(
      loadBlobDiffAsyncOp = failure(LoadBlobDiff.Failure.Unknown)
    )
  }

  fun loadBlobDiffInformation(): ContentViewerModel {
    return copy(
      loadBlobDiffInformationAsyncOp = inFlight()
    )
  }

  fun loadBlobDiff(): ContentViewerModel {
    return copy(
      loadBlobDiffAsyncOp = inFlight()
    )
  }
}
