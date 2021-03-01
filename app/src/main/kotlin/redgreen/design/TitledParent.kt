package redgreen.design

import io.redgreen.timelapse.foo.fastLazy
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import redgreen.dawn.extentions.backgroundFillRoundedCorners

open class TitledParent : GridPane() {
  private companion object {
    private const val ROW_HEIGHT = 32.0
    private const val PADDING = 8.0

    private const val FONT_FAMILY = "Roboto Medium"
    private const val FONT_SIZE = 13.0

    private const val HEX_TITLE_BACKGROUND = "#E2E6EC"
  }

  private val column = VBox()

  private val titleLabel by fastLazy {
    Label().apply {
      prefHeight = ROW_HEIGHT
      padding = Insets(PADDING)
      backgroundFillRoundedCorners(HEX_TITLE_BACKGROUND)
      Platform.runLater { font = Font.font(FONT_FAMILY, FONT_SIZE) }
    }
  }

  init {
    column.prefWidthProperty().bind(widthProperty())
    titleLabel.prefWidthProperty().bind(column.widthProperty())
  }

  fun setContent(title: String, content: Node) {
    addRow(0, column)

    with(column.children) {
      addAll(titleLabel.apply { text = title }, content)
    }
  }
}
