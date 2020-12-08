package io.redgreen.timelapse.openrepo

import io.redgreen.javafx.Fonts
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

class LargeButton(
  private val title: String,
  private val subtitle: String
) : StackPane() {
  companion object {
    private const val PREFERRED_WIDTH = 560.0
    private const val PREFERRED_HEIGHT = 60.0

    private val titleColor = Color.web("#000000")
    private val subtitleColor = Color.web("#8F8F8F")
  }

  init {
    background = Background(BackgroundFill(
      Color.web("#DCEDFE"),
      CornerRadii(4.0),
      Insets.EMPTY
    ))

    val vBox = VBox().apply {
      val titleLabel = Label(title).apply {
        font = Fonts.robotoRegular(14)
        textFill = titleColor
      }

      val subtitleLabel = Label(subtitle).apply {
        font = Fonts.robotoRegular(12)
        textFill = subtitleColor
      }

      spacing = 2.0

      children.addAll(titleLabel, subtitleLabel)
      padding = Insets(0.0, 16.0, 0.0, 16.0)
    }

    val group = Group(vBox)
    children.add(group)
    setAlignment(group, Pos.CENTER_LEFT)

    prefWidth = PREFERRED_WIDTH
    prefHeight = PREFERRED_HEIGHT
  }
}
