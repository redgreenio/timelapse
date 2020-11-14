package io.redgreen.timelapse.ui

import io.redgreen.timelapse.diff.DiffViewer
import io.redgreen.timelapse.diff.FormattedDiff
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.layout.Pane
import kotlin.LazyThreadSafetyMode.NONE

class CodeViewer : JFXPanel(), DiffDisplay { // FIXME: 14-11-2020 Consider getting rid of this shell class!
  override val codeComponent by lazy { this }

  private val diffViewer by lazy(NONE) { DiffViewer() }

  init {
    Platform.runLater {
      scene = Scene(Pane().apply {
        diffViewer.prefWidthProperty().bind(widthProperty())
        diffViewer.prefHeightProperty().bind(heightProperty())

        children.add(diffViewer)
      })
    }
  }

  override fun showDiff(diff: FormattedDiff) {
    diffViewer.showDiff(diff)
  }
}
