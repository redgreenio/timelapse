package io.redgreen.timelapse.ui

import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import java.awt.Color
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.LineBorder

private const val TITLE_BACKGROUND_COLOR = 0x808080
private const val TITLE_FOREGROUND_COLOR = 0xffffff
private const val TITLE_BORDER_THICKNESS = 10

class TitledCodeTextPane : JPanel(BorderLayout()), DiffDisplay by CodeViewer() {
  private val titleLabel = JLabel().apply {
    isOpaque = true
    foreground = Color(TITLE_FOREGROUND_COLOR)
    background = Color(TITLE_BACKGROUND_COLOR)
    border = LineBorder(Color(TITLE_BACKGROUND_COLOR), TITLE_BORDER_THICKNESS)
  }

  init {
    add(titleLabel, NORTH)
    add(codeComponent, CENTER)
  }

  fun setTitle(title: String) {
    titleLabel.text = title
  }
}
