package io.redgreen.timelapse.ui

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color.WHITE

private const val TITLE_BACKGROUND_COLOR = "#808080"
private const val TITLE_PADDING = 10.0

private val TITLE_FOREGROUND_COLOR = WHITE

class TitledCodeTextPane : JFXPanel(), DiffDisplay by CodeViewer() {
  private val titleLabel = Label().apply {
    isOpaque = true
    textFill = TITLE_FOREGROUND_COLOR
    padding = Insets(TITLE_PADDING)
    style = "-fx-background-color: $TITLE_BACKGROUND_COLOR; -fx-font-weight: bold"
  }

  init {
    scene = Scene(BorderPane().apply {
      val borderPane = this
      top = titleLabel.apply { prefWidthProperty().bind(borderPane.widthProperty()) }
      center = codeNode
    })
  }

  fun setTitle(title: String) {
    Platform.runLater {
      titleLabel.text = title
    }
  }
}
