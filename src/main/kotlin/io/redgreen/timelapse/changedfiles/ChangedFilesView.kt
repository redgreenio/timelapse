package io.redgreen.timelapse.changedfiles

interface ChangedFilesView {
  fun showMessage(message: ChangedFilesViewMessage)
  fun hideMessage()
  fun showLoading(show: Boolean)
  fun showChangedFilesList(show: Boolean)
  fun displayChangedFiles(filePaths: ChangedFiles)
}
