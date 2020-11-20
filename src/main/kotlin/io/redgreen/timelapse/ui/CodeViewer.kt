package io.redgreen.timelapse.ui

import io.redgreen.timelapse.diff.DiffViewer
import io.redgreen.timelapse.diff.FormattedDiff
import io.redgreen.timelapse.foo.fastLazy
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.layout.Region

class CodeViewer : Region(), DiffDisplay { // FIXME: 14-11-2020 Consider getting rid of this shell class!
  override val codeNode: Node by lazy { this }

  private val diffViewer by fastLazy { DiffViewer() }

  init {
    diffViewer.prefWidthProperty().bind(widthProperty())
    diffViewer.prefHeightProperty().bind(heightProperty())
    children.add(diffViewer)
  }

  override fun showDiff(diff: FormattedDiff) {
    Platform.runLater {
      diffViewer.showDiff(diff)
    }
  }

  override fun focusOnDiff() {
    diffViewer.webView.requestFocus()
  }
}
