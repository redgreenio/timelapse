package io.redgreen.timelapse.changedfiles.view

import io.redgreen.timelapse.vcs.ChangedFile

interface ChangedFilesView {
  fun showMessage(message: ChangedFilesViewMessage)
  fun hideMessage()
  fun setLoadingVisibility(visible: Boolean)
  fun setChangedFilesListVisibility(visible: Boolean)
  fun showChangedFiles(changedFiles: List<ChangedFile>)
}
