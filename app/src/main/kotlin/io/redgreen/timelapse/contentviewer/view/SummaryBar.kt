package io.redgreen.timelapse.contentviewer.view

import io.redgreen.timelapse.visuals.RoundedCornerLabel
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextFlow

class SummaryBar : VBox() {
  companion object {
    private const val NO_PADDING = 0.0
    private const val ROW_2_TOP_PADDING = 2.0
    private const val LABEL_SPACING = 4.0
    private const val DEFAULT_PADDING = 8.0
    private const val PREFERRED_BAR_HEIGHT = 52.0

    private const val VISIBLE_COMMIT_ID_CHAR_COUNT = 8

    // TODO: 05-12-2020 Extract to a `Theme` class
    private val barBackgroundColor = Color.LIGHTGREY
    private val insertionsBackgroundColor = Color.rgb(0xbe, 0xf5, 0xcb)
    private val deletionsBackgroundColor = Color.rgb(0xff, 0xdc, 0xe0)
    private val filesChangedBackgroundColor = Color.WHITESMOKE
    private val commitIdTextColor = Color.CORNFLOWERBLUE
  }

  private val fileNameLabel = Label().apply {
    style = "-fx-font-weight: bold;"
    padding = Insets(NO_PADDING, LABEL_SPACING, NO_PADDING, NO_PADDING)
  }

  private val deletionsLabel = RoundedCornerLabel(deletionsBackgroundColor)
  private val insertionsLabel = RoundedCornerLabel(insertionsBackgroundColor)
  private val filesChangedLabel = RoundedCornerLabel(filesChangedBackgroundColor)

  private val commitIdText = Text().apply {
    fill = commitIdTextColor
    style = "-fx-font-weight: bold; -fx-cursor: hand;"
    font = Font.font("monospace")
  }

  private val commitMessageText = Text()

  internal var onCommitIdClicked: (() -> Unit)? = null

  init {
    background = Background(BackgroundFill(barBackgroundColor, CornerRadii.EMPTY, Insets.EMPTY))

    val hBoxRow = HBox(fileNameLabel, deletionsLabel, insertionsLabel, filesChangedLabel).apply {
      spacing = LABEL_SPACING
      padding = Insets(DEFAULT_PADDING, DEFAULT_PADDING, NO_PADDING, DEFAULT_PADDING)
      prefWidthProperty().bind(widthProperty())
    }

    val textFlowRow = TextFlow(commitIdText, commitMessageText).apply {
      padding = Insets(ROW_2_TOP_PADDING, DEFAULT_PADDING, NO_PADDING, DEFAULT_PADDING)
    }

    commitIdText.setOnMouseClicked { onCommitIdClicked?.invoke() }

    children.addAll(hBoxRow, textFlowRow)

    prefHeight = PREFERRED_BAR_HEIGHT
  }

  fun setFileName(filePath: String) {
    fileNameLabel.text = filePath
  }

  fun setDeletions(deletions: Int) {
    with(deletionsLabel) {
      text = "-$deletions"
      val deletionsText = if (deletions == 1) "1 deletion" else "$deletions deletions"
      tooltip = Tooltip(deletionsText)
    }
  }

  fun setInsertions(insertions: Int) {
    with(insertionsLabel) {
      text = "+$insertions"
      val insertionsText = if (insertions == 1) "1 insertion" else "$insertions insertions"
      tooltip = Tooltip(insertionsText)
    }
  }

  fun setFilesChanged(filesChanged: Int) {
    with(filesChangedLabel) {
      text = "$filesChanged"
      val filesChangedText = when (filesChanged) {
        0 -> "No other files changed"
        1 -> "1 other file changed"
        else -> "$filesChanged other files changed"
      }
      tooltip = Tooltip(filesChangedText)
    }
  }

  fun setCommitId(commitId: String) {
    with(commitIdText) {
      text = commitId.take(VISIBLE_COMMIT_ID_CHAR_COUNT)
      Tooltip.install(this, Tooltip("$commitId (click to copy)"))
    }
  }

  fun setCommitMessage(message: String) {
    commitMessageText.text = " • $message"
  }
}
