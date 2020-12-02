package io.redgreen.timelapse.contentviewer

import com.spotify.mobius.Next
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update

object ContentViewerUpdate : Update<ContentViewerModel, ContentViewerEvent, ContentViewerEffect> {
  override fun update(
    model: ContentViewerModel,
    event: ContentViewerEvent
  ): Next<ContentViewerModel, ContentViewerEffect> {
    val (selectedFilePath, commitId) = event as FileAndRevisionSelected
    return next(
      model.fileAndRevisionSelected(selectedFilePath, commitId),
      setOf(
        LoadBlobDiffInformation(selectedFilePath, commitId),
        LoadBlobDiff(selectedFilePath, commitId)
      )
    )
  }
}
