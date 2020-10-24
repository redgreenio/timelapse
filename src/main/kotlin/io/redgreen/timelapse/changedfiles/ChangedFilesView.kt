package io.redgreen.timelapse.changedfiles

import io.redgreen.timelapse.vcs.ChangedFile

interface ChangedFilesView {
  fun showMessage(message: ChangedFilesViewMessage)
  fun hideMessage()
  fun showLoading(show: Boolean)
  fun showChangedFilesList(show: Boolean)
  fun displayChangedFiles(changedFiles: List<ChangedFile>)
}
