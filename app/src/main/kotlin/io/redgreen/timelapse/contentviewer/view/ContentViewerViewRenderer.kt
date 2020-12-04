package io.redgreen.timelapse.contentviewer.view

import io.redgreen.timelapse.contentviewer.BlobDiffInformation
import io.redgreen.timelapse.contentviewer.ContentViewerModel
import io.redgreen.timelapse.domain.BlobDiff
import io.redgreen.timelapse.mobius.AsyncOp
import io.redgreen.timelapse.mobius.view.ViewRenderer

class ContentViewerViewRenderer(
  private val view: ContentViewerView
) : ViewRenderer<ContentViewerModel> {
  override fun render(model: ContentViewerModel) {
    when {
      isLoadingContent(model.loadBlobDiffAsyncOp) -> view.showLoading(model)
      isContentLoaded(model) -> view.showContent(model)
      else -> view.showNothingSelected()
    }
  }

  private fun isLoadingContent(asyncOp: AsyncOp<BlobDiff, Any>): Boolean =
    asyncOp.value is AsyncOp.InFlight

  private fun ContentViewerView.showLoading(model: ContentViewerModel) {
    showFileNameLabel(true)
    showDeletionsInsertionsAndFilesChangedLabels(true)
    showCommitIdLabel(true)
    setFileName(model.selectedFilePath!!)
    setCommitId(model.commitId!!)
    displayLoadingMessage()
  }

  private fun isContentLoaded(model: ContentViewerModel): Boolean =
    model.loadBlobDiffAsyncOp.value is AsyncOp.Content && model.loadBlobDiffInformationAsyncOp is AsyncOp.Content

  private fun ContentViewerView.showContent(model: ContentViewerModel) {
    val blobDiffInformation = (model.loadBlobDiffInformationAsyncOp as AsyncOp.Content<BlobDiffInformation>).content
    val blobDiff = (model.loadBlobDiffAsyncOp as AsyncOp.Content<BlobDiff>).content
    val (_, _, message, deletions, insertions, changedFiles) = blobDiffInformation

    setDeletionsInsertionsAndFilesChanged(deletions, insertions, changedFiles - 1)
    showCommitMessageLabel(true)
    setCommitMessage(message)
    displayContent(blobDiff)
  }

  private fun ContentViewerView.showNothingSelected() {
    showFileNameLabel(false)
    showDeletionsInsertionsAndFilesChangedLabels(false)
    showCommitIdLabel(false)
    showCommitMessageLabel(false)
    displaySelectFileMessage()
  }
}
