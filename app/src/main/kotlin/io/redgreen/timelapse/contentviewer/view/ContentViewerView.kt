package io.redgreen.timelapse.contentviewer.view

import io.redgreen.timelapse.domain.BlobDiff

interface ContentViewerView {
  fun showCommitMessageLabel(show: Boolean)
  fun showDeletionsInsertionsAndFilesChangedLabels(show: Boolean)
  fun showFileNameLabel(show: Boolean)
  fun showCommitIdLabel(show: Boolean)
  fun displaySelectFileMessage()
  fun setFileName(filePath: String)
  fun setCommitId(commitId: String)
  fun displayLoadingMessage()

  fun setDeletionsInsertionsAndFilesChanged(
    deletions: Int,
    insertions: Int,
    changedFiles: Int
  )

  fun setCommitMessage(message: String)
  fun displayContent(blobDiff: BlobDiff)
}
