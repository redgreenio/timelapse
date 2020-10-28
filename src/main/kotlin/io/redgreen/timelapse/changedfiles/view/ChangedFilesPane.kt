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
import org.eclipse.jgit.lib.Repository
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import javax.swing.DefaultListModel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel
import kotlin.LazyThreadSafetyMode.NONE

class ChangedFilesPane(
  gitRepository: Repository,
  private val readingAreaContract: ReadingAreaContract
) : JPanel(BorderLayout()), ChangedFilesView, Connectable<ChangedFilesModel, ChangedFilesEvent> {
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

  private val changedFilesList = JList<ChangedFile>().apply {
    selectionMode = ListSelectionModel.SINGLE_SELECTION
    cellRenderer = ChangedFileListCellRenderer()
  }

  init {
    with(changedFilesList) {
      changedFilesList.addListSelectionListener { event ->
        val stableSelection = selectedIndex != -1 && !event.valueIsAdjusting
        if (!stableSelection) return@addListSelectionListener
        eventSource.notify(ChangedFileSelected(selectedIndex))
      }
    }

    add(JScrollPane(changedFilesList), CENTER)

    with(loopController) {
      connect(Connectables.contramap({ it }, this@ChangedFilesPane))
      start()
    }
  }

  override fun showMessage(message: ChangedFilesViewMessage) {
    debug { "showMessage stub! $message" }
    if (message == NO_OTHER_FILES_CHANGED) {
      changedFilesList.model = DefaultListModel()
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
    val changesListModel = DefaultListModel<ChangedFile>()
    changedFiles.onEach { changesListModel.addElement(it) }
    changedFilesList.model = changesListModel
  }

  fun focusOnList() {
    changedFilesList.requestFocus()
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
