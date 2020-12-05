package io.redgreen.timelapse.changedfiles.view

import io.redgreen.timelapse.changedfiles.ChangedFileSelected
import io.redgreen.timelapse.changedfiles.ChangedFilesEffectHandler
import io.redgreen.timelapse.changedfiles.ChangedFilesModel
import io.redgreen.timelapse.changedfiles.ChangedFilesUpdate
import io.redgreen.timelapse.changedfiles.FileAndRevisionSelected
import io.redgreen.timelapse.changedfiles.contracts.ReadingAreaContract
import io.redgreen.timelapse.changedfiles.view.ChangedFilesViewMessage.NO_OTHER_FILES_CHANGED
import io.redgreen.timelapse.foo.fastLazy
import io.redgreen.timelapse.mobius.MobiusDelegate
import io.redgreen.timelapse.vcs.ChangedFile
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import io.redgreen.timelapse.vcs.ChangedFile.Deletion
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import io.redgreen.timelapse.vcs.ChangedFile.Rename
import io.redgreen.timelapse.vcs.git.GitRepositoryService
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode.SINGLE
import javafx.scene.input.KeyCode.ENTER
import javafx.scene.layout.BorderPane
import org.eclipse.jgit.lib.Repository

class ChangedFilesPane(
  gitRepository: Repository,
  private val readingAreaContract: ReadingAreaContract
) : BorderPane(), ChangedFilesView {
  private val repositoryService = GitRepositoryService(gitRepository)

  private val mobiusDelegate by fastLazy {
    MobiusDelegate(
      ChangedFilesModel.noFileAndRevisionSelected(),
      ChangedFilesUpdate,
      ChangedFilesEffectHandler.from(repositoryService, readingAreaContract),
      ChangedFilesViewRenderer(this)
    )
  }

  private val titleLabel by fastLazy { Label().apply { style = "-fx-font-weight: bold" } }

  private val changedFilesListView by lazy {
    ListView<ChangedFile>().apply {
      isEditable = false
      selectionModel.selectionMode = SINGLE

      setCellFactory {
        object : ListCell<ChangedFile>() {
          override fun updateItem(changedFile: ChangedFile?, empty: Boolean) {
            super.updateItem(changedFile, empty)

            text = if (empty || item == null) {
              null
            } else {
              changedFile?.filePath
            }

            style = if (changedFile != null) {
              "-fx-control-inner-background: rgba(${colorForChangedFile(changedFile)});"
            } else {
              "-fx-control-inner-background: rgba(255, 255, 255, 1.0);"
            }
          }

          private fun colorForChangedFile(changedFile: ChangedFile): String {
            val alpha = 0.33
            return when (changedFile) {
              is Addition -> "94, 186, 125, $alpha"
              is Modification -> "94, 154, 186, $alpha"
              is Deletion -> "186, 94, 94, $alpha"
              is Rename -> "169, 94, 186, $alpha"
            }
          }
        }
      }

      setOnKeyPressed { event ->
        if (event.code == ENTER) {
          sendChangedFileSelectedEvent(selectionModel.selectedIndex)
        }
      }
    }
  }

  init {
    with(changedFilesListView) {
      selectionModel.selectedItemProperty().addListener { _, _, _ ->
        sendChangedFileSelectedEvent(selectionModel.selectedIndex)
      }

      setOnMouseClicked {
        sendChangedFileSelectedEvent(selectionModel.selectedIndex)
      }
    }

    mobiusDelegate.start()

    top = titleLabel
    center = changedFilesListView
    updateTitle()
  }

  override fun showMessage(message: ChangedFilesViewMessage) {
    // TODO: 18-11-2020, Define and refine UI
    if (message == NO_OTHER_FILES_CHANGED) {
      with(changedFilesListView) {
        items.clear()
        selectionModel.clearSelection()
      }
      updateTitle()
    }
  }

  override fun hideMessage() {
    // TODO: 18-11-2020, Define and refine UI
  }

  override fun setLoadingVisibility(visible: Boolean) {
    // TODO: 18-11-2020, Define and refine UI
  }

  override fun setChangedFilesListVisibility(visible: Boolean) {
    // TODO: 18-11-2020, Define and refine UI
  }

  override fun showChangedFiles(changedFiles: List<ChangedFile>) {
    with(changedFilesListView) {
      items.clear()
      items.addAll(changedFiles)
      selectionModel.clearSelection()
      scrollTo(0)
    }

    updateTitle(changedFiles.size)
  }

  fun focusOnList() {
    changedFilesListView.requestFocus()
  }

  fun selectFileAndRevision(filePath: String, commitId: String) {
    mobiusDelegate.notify(FileAndRevisionSelected(filePath, commitId))
  }

  private fun sendChangedFileSelectedEvent(selectedIndex: Int) {
    if (selectedIndex == -1) {
      return
    }
    mobiusDelegate.notify(ChangedFileSelected(selectedIndex))
  }

  private fun updateTitle(changedFilesCount: Int = 0) {
    val title = if (changedFilesCount == 0) {
      "Changed Files (None)"
    } else {
      "Changed Files ($changedFilesCount more)"
    }

    titleLabel.text = " • $title • "
  }
}
