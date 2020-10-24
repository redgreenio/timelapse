package io.redgreen.timelapse.ui

import io.redgreen.timelapse.vcs.ChangedFile
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList
import javax.swing.ListCellRenderer

internal typealias CommitId = String

class FileChangeListCellRenderer : ListCellRenderer<Pair<ChangedFile, Pair<CommitId?, CommitId>>> {
  private val defaultRenderer = DefaultListCellRenderer()

  override fun getListCellRendererComponent(
    list: JList<out Pair<ChangedFile, Pair<CommitId?, CommitId>>>,
    value: Pair<ChangedFile, Pair<CommitId?, CommitId>>,
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
