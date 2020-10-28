package io.redgreen.timelapse.changedfiles.view

import io.redgreen.timelapse.vcs.ChangedFile
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList
import javax.swing.ListCellRenderer

class ChangedFileListCellRenderer : ListCellRenderer<ChangedFile> {
  private val defaultRenderer = DefaultListCellRenderer()

  override fun getListCellRendererComponent(
    list: JList<out ChangedFile>,
    value: ChangedFile,
    index: Int,
    isSelected: Boolean,
    cellHasFocus: Boolean
  ): Component {
    return defaultRenderer.getListCellRendererComponent(
      list,
      value.filePath,
      index,
      isSelected,
      cellHasFocus
    )
  }
}
