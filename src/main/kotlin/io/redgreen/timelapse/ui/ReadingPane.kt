package io.redgreen.timelapse.ui

import io.redgreen.timelapse.diff.DiffLine
import io.redgreen.timelapse.diff.DiffLine.Deletion
import io.redgreen.timelapse.diff.DiffLine.Insertion
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
  private val mainCodeTextPane = TitledCodeTextPane()
  private val overlappingModalPanel = JPanel()

  private val overlappingCodeTextPane = TitledCodeTextPane()

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

  fun showMainDiff(filePath: String, diffLines: List<DiffLine>) {
    moveToFront(mainCodeTextPane)
    with(mainCodeTextPane) {
      setTitle("$filePath [${getInsertionsDeletionsSummaryText(diffLines)}]")
      showDiff(diffLines)
    }
  }

  fun showOverlappingDiff(title: String, diffLines: List<DiffLine>) {
    overlappingModalPanel.isVisible = true
    moveToFront(overlappingModalPanel)

    with(overlappingCodeTextPane) {
      setTitle("$title [${getInsertionsDeletionsSummaryText(diffLines)}]")
      showDiff(diffLines)
    }
  }

  fun dismissOverlap() {
    moveToFront(mainCodeTextPane)

    overlappingModalPanel.isVisible = false
    moveToBack(overlappingModalPanel)
  }

  fun isShowingOverlap(): Boolean =
    getIndexOf(mainCodeTextPane) > getIndexOf(overlappingModalPanel)

  fun focusOnOverlap() {
    overlappingCodeTextPane.requestFocus()
  }

  private fun getInsertionsDeletionsSummaryText(diffLines: List<DiffLine>): String {
    val deletionsCount = diffLines.filterIsInstance<Deletion>().count()
    val insertionsCount = diffLines.filterIsInstance<Insertion>().count()

    return when {
      deletionsCount > 0 && insertionsCount > 0 -> "+$insertionsCount, -$deletionsCount"
      deletionsCount == 0 && insertionsCount == 0 -> "?? Empty Commit ??"
      deletionsCount == 0 -> "+$insertionsCount"
      insertionsCount == 0 -> "-$deletionsCount"
      else -> throw IllegalStateException("Insertions $insertionsCount, Deletions $deletionsCount")
    }
  }
}
