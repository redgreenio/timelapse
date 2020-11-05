package io.redgreen.timelapse.changedfiles.view

import com.spotify.mobius.Connectable
import com.spotify.mobius.Connection
import com.spotify.mobius.Mobius
import com.spotify.mobius.extras.Connectables
import com.spotify.mobius.functions.Consumer
import com.spotify.mobius.rx3.RxMobius
import io.redgreen.timelapse.changedfiles.ChangedFileSelected
import io.redgreen.timelapse.changedfiles.ChangedFilesEffectHandler
import io.redgreen.timelapse.changedfiles.ChangedFilesEvent
import io.redgreen.timelapse.changedfiles.ChangedFilesInit
import io.redgreen.timelapse.changedfiles.ChangedFilesModel
import io.redgreen.timelapse.changedfiles.ChangedFilesUpdate
import io.redgreen.timelapse.changedfiles.FileAndRevisionSelected
import io.redgreen.timelapse.changedfiles.contracts.ReadingAreaContract
import io.redgreen.timelapse.changedfiles.view.ChangedFilesViewMessage.NO_OTHER_FILES_CHANGED
import io.redgreen.timelapse.debug
import io.redgreen.timelapse.mobius.DeferredEventSource
import io.redgreen.timelapse.vcs.ChangedFile
import io.redgreen.timelapse.vcs.git.GitRepositoryService
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode.SINGLE
import javafx.scene.layout.BorderPane
import org.eclipse.jgit.lib.Repository
import javax.swing.BorderFactory
import kotlin.LazyThreadSafetyMode.NONE

class ChangedFilesPane(
  gitRepository: Repository,
  private val readingAreaContract: ReadingAreaContract
) : JFXPanel(), ChangedFilesView, Connectable<ChangedFilesModel, ChangedFilesEvent> {
  private val repositoryService = GitRepositoryService(gitRepository)

  private val eventSource = DeferredEventSource<ChangedFilesEvent>()

  private val loopController by lazy(NONE) {
    val effectHandler = ChangedFilesEffectHandler
      .from(repositoryService, readingAreaContract)

    val loop = RxMobius
      .loop(ChangedFilesUpdate, effectHandler)
      .eventSource(eventSource)

    Mobius.controller(loop, ChangedFilesModel.noFileAndRevisionSelected(), ChangedFilesInit)
  }

  private val viewRenderer = ChangedFilesViewRenderer(this)

  private val changedFilesListView by lazy {
    ListView<ChangedFile>().apply {
      isEditable = false
      selectionModel.selectionMode = SINGLE

      setCellFactory {
        object : ListCell<ChangedFile>() {
          override fun updateItem(changedFile: ChangedFile?, empty: Boolean) {
            super.updateItem(changedFile, empty)

            Platform.runLater {
              text = if (empty || item == null) {
                null
              } else {
                changedFile?.filePath
              }
            }
          }
        }
      }
    }
  }

  init {
    with(changedFilesListView) {
      selectionModel.selectedItemProperty().addListener { _, _, _ ->
        eventSource.notify(ChangedFileSelected(selectionModel.selectedIndex))
      }
    }

    with(loopController) {
      connect(Connectables.contramap({ it }, this@ChangedFilesPane))
      start()
    }

    scene = Scene(BorderPane().apply {
      center = changedFilesListView
    })

    border = BorderFactory.createTitledBorder("Changed Files")
  }

  override fun showMessage(message: ChangedFilesViewMessage) {
    debug { "showMessage stub! $message" }
    if (message == NO_OTHER_FILES_CHANGED) {
      with(changedFilesListView) {
        items.clear()
        selectionModel.clearSelection()
      }
    }
  }

  override fun hideMessage() {
    debug { "hideMessage stub!" }
  }

  override fun setLoadingVisibility(visible: Boolean) {
    debug { "showLoading stub! $visible" }
  }

  override fun setChangedFilesListVisibility(visible: Boolean) {
    debug { "showChangedFilesList stub! $visible" }
  }

  override fun showChangedFiles(changedFiles: List<ChangedFile>) {
    with(changedFilesListView) {
      Platform.runLater { // TODO View updates should be observed on JavaFx UI thread
        items.clear()
        items.addAll(changedFiles)
        selectionModel.clearSelection()
        scrollTo(0)
      }
    }
  }

  fun focusOnList() {
    Platform.runLater { changedFilesListView.requestFocus() }
  }

  fun selectFileAndRevision(filePath: String, commitId: String) {
    eventSource.notify(FileAndRevisionSelected(filePath, commitId))
  }

  override fun connect(output: Consumer<ChangedFilesEvent>): Connection<ChangedFilesModel> {
    return object : Connection<ChangedFilesModel> {
      override fun accept(value: ChangedFilesModel) {
        viewRenderer.render(value)
      }

      override fun dispose() {
        // No-op
      }
    }
  }
}
