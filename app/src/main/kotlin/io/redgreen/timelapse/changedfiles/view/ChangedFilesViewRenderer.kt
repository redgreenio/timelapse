package io.redgreen.timelapse.changedfiles.view

import io.redgreen.timelapse.changedfiles.ChangedFilesModel
import io.redgreen.timelapse.changedfiles.view.ChangedFilesViewMessage.NO_FILE_AND_REVISION_SELECTED
import io.redgreen.timelapse.changedfiles.view.ChangedFilesViewMessage.NO_OTHER_FILES_CHANGED
import io.redgreen.timelapse.changedfiles.view.ChangedFilesViewMessage.RETRY_GETTING_CHANGED_FILES
import io.redgreen.timelapse.mobius.AsyncOp.Content
import io.redgreen.timelapse.mobius.AsyncOp.Failure
import io.redgreen.timelapse.mobius.AsyncOp.InFlight
import io.redgreen.timelapse.mobius.view.ViewRenderer
import io.redgreen.timelapse.vcs.ChangedFile

class ChangedFilesViewRenderer(
  private val view: ChangedFilesView
) : ViewRenderer<ChangedFilesModel> {
  override fun render(model: ChangedFilesModel) = when (model.getChangedFilesAsyncOp.value) {
    is InFlight -> renderGettingChangedFiles()
    is Content -> renderChangedFiles((model.getChangedFilesAsyncOp as Content<List<ChangedFile>>).content)
    is Failure -> renderUnableToGetChangedFiles()
    else -> renderNoFileAndRevisionSelected()
  }

  private fun renderGettingChangedFiles() {
    with(view) {
      setLoadingVisibility(true)
      hideMessage()
      setChangedFilesListVisibility(false)
    }
  }

  private fun renderChangedFiles(changedFiles: List<ChangedFile>) {
    with(view) {
      setLoadingVisibility(false)

      if (changedFiles.isEmpty()) {
        showMessage(NO_OTHER_FILES_CHANGED)
        setChangedFilesListVisibility(false)
      } else {
        hideMessage()
        showChangedFiles(changedFiles)
      }
    }
  }

  private fun renderUnableToGetChangedFiles() {
    with(view) {
      showMessage(RETRY_GETTING_CHANGED_FILES)
      setLoadingVisibility(false)
      setChangedFilesListVisibility(false)
    }
  }

  private fun renderNoFileAndRevisionSelected() {
    with(view) {
      setLoadingVisibility(false)
      showMessage(NO_FILE_AND_REVISION_SELECTED)
      setChangedFilesListVisibility(false)
    }
  }
}
