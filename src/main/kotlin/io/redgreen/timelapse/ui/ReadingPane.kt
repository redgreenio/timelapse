package io.redgreen.timelapse.ui

import io.redgreen.timelapse.visuals.DiffSpan
import javax.swing.JLayeredPane
import javax.swing.OverlayLayout

class ReadingPane : JLayeredPane() {
  private val mainCodeTextPane = CodeTextPane()

  init {
    layout = OverlayLayout(this)
    add(mainCodeTextPane, DEFAULT_LAYER)
  }

  fun showMainDiff(diffSpans: List<DiffSpan>) {
    mainCodeTextPane.showDiff(diffSpans)
  }
}
