package io.redgreen.timelapse.changedfiles.view

import io.redgreen.timelapse.changedfiles.contracts.ReadingAreaContract
import io.redgreen.timelapse.debug
import io.redgreen.timelapse.vcs.ChangedFile
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import javax.swing.DefaultListModel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel

class ChangedFilesPane(private val contract: ReadingAreaContract) : JPanel(BorderLayout()) {
  private val changedFilesList = JList<Pair<ChangedFile, CommitId>>().apply {
    selectionMode = ListSelectionModel.SINGLE_SELECTION
    cellRenderer = ChangedFileListCellRenderer()
  }

  init {
    with(changedFilesList) {
      changedFilesList.addListSelectionListener { event ->
        val stableSelection = selectedIndex != -1 && !event.valueIsAdjusting
        if (stableSelection) {
          val (changedFile, commitId) = model.getElementAt(selectedIndex)

          debug { "Selected list item: $changedFile" }

          contract.showChangedFileDiff(commitId, changedFile)
        }
      }
    }

    add(JScrollPane(changedFilesList), CENTER)
  }

  fun setModel(model: DefaultListModel<Pair<ChangedFile, CommitId>>) {
    changedFilesList.model = model
  }

  fun focusOnList() {
    changedFilesList.requestFocus()
  }
}
