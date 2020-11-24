package io.redgreen.timelapse.ui

import io.redgreen.timelapse.diff.DiffLine
import io.redgreen.timelapse.diff.DiffLine.Deletion
import io.redgreen.timelapse.diff.DiffLine.Insertion
import io.redgreen.timelapse.diff.FormattedDiff
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane

private const val TRANSLUCENT_MODAL_WIDTH = 150.0

class ReadingPane : StackPane() {
  private val mainCodeTextPane = TitledCodeTextPane()
  private val overlappingCodeTextPane = TitledCodeTextPane()

  private val modalPane = BorderPane().apply {
    isVisible = false

    left = Pane().apply {
      prefWidth = TRANSLUCENT_MODAL_WIDTH
      style = "-fx-background-color: rgba(0, 0, 0, 0.3);"
    }
    center = overlappingCodeTextPane
  }

  init {
    children.addAll(mainCodeTextPane, modalPane)
  }

  fun showMainDiff(filePath: String, diff: FormattedDiff) {
    modalPane.isVisible = false
    with(mainCodeTextPane) {
      setTitle("$filePath [${getInsertionsDeletionsSummaryText(diff.lines)}]")
      showDiff(diff)
    }
  }

  fun showOverlappingDiff(title: String, diff: FormattedDiff) {
    modalPane.isVisible = true

    with(overlappingCodeTextPane) {
      setTitle("$title [${getInsertionsDeletionsSummaryText(diff.lines)}]")
      showDiff(diff)
    }
  }

  fun dismissOverlap() {
    modalPane.isVisible = false
  }

  fun isShowingOverlap(): Boolean =
    modalPane.isVisible

  fun focusOnOverlap() {
    overlappingCodeTextPane.requestFocus()
  }

  private fun getInsertionsDeletionsSummaryText(diffLines: List<DiffLine>): String {
    val deletionsCount = diffLines.filterIsInstance<Deletion>().count()
    val insertionsCount = diffLines.filterIsInstance<Insertion>().count()
    return "-$deletionsCount, +$insertionsCount"
  }
}
