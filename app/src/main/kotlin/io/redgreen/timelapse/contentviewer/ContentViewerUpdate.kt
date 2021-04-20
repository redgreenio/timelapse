package io.redgreen.timelapse.contentviewer

import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update
import io.redgreen.architecture.mobius.AsyncOp

object ContentViewerUpdate : Update<ContentViewerModel, ContentViewerEvent, ContentViewerEffect> {
  override fun update(
    model: ContentViewerModel,
    event: ContentViewerEvent
  ): Next<ContentViewerModel, ContentViewerEffect> {
    return when (event) {
      is FileAndRevisionSelected -> attemptToLoadBlobDiffAndInformation(event, model)
      is BlobDiffInformationLoaded -> next(model.blobDiffInformationLoaded(event.blobDiffInformation))
      is UnableToLoadBlobDiffInformation -> next(model.unableToLoadBlobDiffInformation())
      is BlobDiffLoaded -> next(model.blobDiffLoaded(event.blobDiff))
      is UnableToLoadBlobDiff -> next(model.unableToLoadBlobDiff())
      is Retry -> retryFailedEffects(model)
      is CommitIdClicked -> dispatch(setOf(CopyCommitIdToClipboard(model.commitId!!)))
    }
  }

  private fun retryFailedEffects(model: ContentViewerModel): Next<ContentViewerModel, ContentViewerEffect> {
    val selectedFilePath = model.selectedFilePath!!
    val commitId = model.commitId!!

    val loadBlobDiffInformationFailed = model.loadBlobDiffInformationAsyncOp is AsyncOp.Failure
    val loadBlobDiffFailed = model.loadBlobDiffAsyncOp is AsyncOp.Failure

    val nextModel: ContentViewerModel
    val nextEffects: Set<ContentViewerEffect>
    when {
      loadBlobDiffInformationFailed && loadBlobDiffFailed -> {
        nextModel = model.loadBlobDiff().loadBlobDiffInformation()
        nextEffects = setOf(
          LoadBlobDiffInformation(selectedFilePath, commitId),
          LoadBlobDiff(model.selectedFilePath, model.commitId)
        )
      }

      loadBlobDiffFailed -> {
        nextModel = model.loadBlobDiff()
        nextEffects = setOf(LoadBlobDiff(selectedFilePath, commitId))
      }

      else -> {
        nextModel = model.loadBlobDiffInformation()
        nextEffects = setOf(LoadBlobDiffInformation(selectedFilePath, commitId))
      }
    }

    return next(nextModel, nextEffects)
  }

  private fun attemptToLoadBlobDiffAndInformation(
    event: FileAndRevisionSelected,
    model: ContentViewerModel
  ): Next<ContentViewerModel, ContentViewerEffect> {
    val (selectedFilePath, commitId) = event
    return next(
      model.fileAndRevisionSelected(selectedFilePath, commitId),
      setOf(LoadBlobDiffInformation(selectedFilePath, commitId), LoadBlobDiff(selectedFilePath, commitId))
    )
  }
}
