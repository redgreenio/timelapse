package io.redgreen.timelapse.changedfiles

import io.redgreen.timelapse.vcs.ChangedFile
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList
import javax.swing.ListCellRenderer

internal typealias CommitId = String

class ChangedFileListCellRenderer : ListCellRenderer<Pair<ChangedFile, CommitId>> {
  private val defaultRenderer = DefaultListCellRenderer()

  override fun getListCellRendererComponent(
    list: JList<out Pair<ChangedFile, CommitId>>,
    value: Pair<ChangedFile, CommitId>,
    index: Int,
    isSelected: Boolean,
    cellHasFocus: Boolean
  ): Component {
    return defaultRenderer.getListCellRendererComponent(
      list,
      value.first.filePath,
      index,
      isSelected,
      cellHasFocus
    )
  }
}
