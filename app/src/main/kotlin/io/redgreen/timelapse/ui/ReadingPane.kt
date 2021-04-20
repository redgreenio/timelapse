package io.redgreen.timelapse.ui

import io.redgreen.timelapse.contentviewer.view.ContentViewerPane
import io.redgreen.timelapse.diff.DiffLine
import io.redgreen.timelapse.diff.DiffLine.Deletion
import io.redgreen.timelapse.diff.DiffLine.Insertion
import io.redgreen.timelapse.diff.FormattedDiff
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import org.eclipse.jgit.lib.Repository

private const val TRANSLUCENT_MODAL_WIDTH = 150.0

class ReadingPane(
  gitRepository: Repository
) : StackPane() {
  private val mainCodeTextPane = ContentViewerPane(gitRepository)
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

  fun showMainDiff(selectedFilePath: String, commitId: String) {
    modalPane.isVisible = false
    mainCodeTextPane.selectFileAndCommitId(selectedFilePath, commitId)
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
