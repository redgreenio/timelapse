package io.redgreen.timelapse.changedfiles

import io.redgreen.timelapse.changedfiles.ChangedFilesViewMessage.NO_FILE_AND_REVISION_SELECTED
import io.redgreen.timelapse.changedfiles.ChangedFilesViewMessage.NO_OTHER_FILES_CHANGED
import io.redgreen.timelapse.changedfiles.ChangedFilesViewMessage.RETRY_GETTING_CHANGED_FILES
import io.redgreen.timelapse.mobius.AsyncOp.Content
import io.redgreen.timelapse.mobius.AsyncOp.Failure
import io.redgreen.timelapse.mobius.AsyncOp.InFlight

class ChangedFilesViewRenderer(
  private val view: ChangedFilesView
) {
  fun render(model: ChangedFilesModel) = when (model.getChangedFilesAsyncOp.instance) {
    is InFlight -> renderGettingChangedFiles()
    is Content -> renderChangedFiles((model.getChangedFilesAsyncOp as Content<ChangedFiles>).content)
    is Failure -> renderUnableToGetChangedFiles()
    else -> renderNoFileAndRevisionSelected()
  }

  private fun renderGettingChangedFiles() {
    with(view) {
      showLoading(true)
      hideMessage()
      showChangedFilesList(false)
    }
  }

  private fun renderChangedFiles(changedFiles: ChangedFiles) {
    with(view) {
      showLoading(false)

      if (changedFiles.isEmpty()) {
        showMessage(NO_OTHER_FILES_CHANGED)
        showChangedFilesList(false)
      } else {
        hideMessage()
        displayChangedFiles(changedFiles)
      }
    }
  }

  private fun renderUnableToGetChangedFiles() {
    with(view) {
      showMessage(RETRY_GETTING_CHANGED_FILES)
      showLoading(false)
      showChangedFilesList(false)
    }
  }

  private fun renderNoFileAndRevisionSelected() {
    with(view) {
      showLoading(false)
      showMessage(NO_FILE_AND_REVISION_SELECTED)
      showChangedFilesList(false)
    }
  }
}
