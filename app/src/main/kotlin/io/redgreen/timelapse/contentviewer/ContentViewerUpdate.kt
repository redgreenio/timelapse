package io.redgreen.timelapse.contentviewer

import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update
import io.redgreen.timelapse.mobius.AsyncOp

object ContentViewerUpdate : Update<ContentViewerModel, ContentViewerEvent, ContentViewerEffect> {
  override fun update(
    model: ContentViewerModel,
    event: ContentViewerEvent
  ): Next<ContentViewerModel, ContentViewerEffect> {
    return when (event) {
      is FileAndRevisionSelected -> {
        val (selectedFilePath, commitId) = event
        next(
          model.fileAndRevisionSelected(selectedFilePath, commitId),
          setOf(LoadBlobDiffInformation(selectedFilePath, commitId), LoadBlobDiff(selectedFilePath, commitId))
        )
      }

      is BlobDiffInformationLoaded -> next(model.blobDiffInformationLoaded(event.blobDiffInformation))

      is UnableToLoadBlobDiffInformation -> next(model.unableToLoadBlobDiffInformation())

      is BlobDiffLoaded -> next(model.blobDiffLoaded(event.blobDiff))

      is UnableToLoadBlobDiff -> next(model.unableToLoadBlobDiff())

      is Retry -> {
        val next = if (model.loadBlobDiffAsyncOp is AsyncOp.Failure && model.loadBlobDiffInformationAsyncOp is AsyncOp.Failure) {
          model.loadBlobDiff().loadBlobDiffInformation() to setOf(LoadBlobDiffInformation(model.selectedFilePath!!, model.commitId!!), LoadBlobDiff(model.selectedFilePath!!, model.commitId!!))
        } else if (model.loadBlobDiffAsyncOp is AsyncOp.Failure) {
          model.loadBlobDiff() to setOf(LoadBlobDiff(model.selectedFilePath!!, model.commitId!!))
        } else {
          model.loadBlobDiffInformation() to setOf(LoadBlobDiffInformation(model.selectedFilePath!!, model.commitId!!))
        }

        next(next.first, next.second)
      }

      is CommitIdClicked -> dispatch(setOf(CopyCommitIdToClipboard(model.commitId!!)))
    }
  }
}
