package io.redgreen.timelapse.contentviewer.view

import io.redgreen.timelapse.domain.BlobDiff

interface ContentViewerView {
  fun fileNameLabelVisible(visible: Boolean)

  fun deletionsInsertionsAndFilesChangedLabelsVisible(visible: Boolean)

  fun commitIdLabelVisible(visible: Boolean)
  fun commitMessageLabelVisible(visible: Boolean)

  fun setFileName(filePath: String)

  fun setDeletionsInsertionsAndFilesChanged(
    deletions: Int,
    insertions: Int,
    changedFiles: Int
  )

  fun setCommitId(commitId: String)
  fun setCommitMessage(message: String)

  fun displaySelectFileMessage()
  fun displayLoadingMessage()
  fun displayContent(blobDiff: BlobDiff)
}
