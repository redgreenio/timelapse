package io.redgreen.timelapse.ui

import io.redgreen.timelapse.visuals.DiffSpan
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.WEST
import java.awt.Color
import java.awt.Dimension
import javax.swing.JLayeredPane
import javax.swing.JPanel
import javax.swing.OverlayLayout

private val MODAL_COLOR = Color(0, 0, 0, 50)
private const val TRANSLUCENT_AREA_WIDTH = 200
private const val MATCH_PARENT = 0

class ReadingPane : JLayeredPane() {
  private val mainCodeTextPane = CodeTextPane()
  private val overlappingModalPanel = JPanel()

  private val overlappingCodeTextPane = CodeTextPane()

  init {
    layout = OverlayLayout(this)
    add(mainCodeTextPane, DEFAULT_LAYER)

    overlappingModalPanel.apply {
      isOpaque = false
      layout = BorderLayout()
      val fakeModalTranslucentPanel = JPanel().apply {
        background = MODAL_COLOR
        preferredSize = Dimension(TRANSLUCENT_AREA_WIDTH, MATCH_PARENT)
      }
      add(overlappingCodeTextPane, CENTER)
      add(fakeModalTranslucentPanel, WEST)
    }
    add(overlappingModalPanel, MODAL_LAYER)
  }

  fun showMainDiff(diffSpans: List<DiffSpan>) {
    moveToFront(mainCodeTextPane)
    mainCodeTextPane.showDiff(diffSpans)
  }

  fun showOverlappingDiff(diffSpans: List<DiffSpan>) {
    moveToFront(overlappingModalPanel)
    overlappingCodeTextPane.showDiff(diffSpans)
  }
}
