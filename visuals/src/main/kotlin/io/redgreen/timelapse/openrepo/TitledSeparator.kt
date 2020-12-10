package io.redgreen.timelapse.openrepo

import io.redgreen.javafx.Fonts
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.paint.Color

private val grayTextFill = Color.web("#8F8F8F")

class TitledSeparator(
  title: String
) : HBox() {
  private val titleLabel = Label(title).apply {
    font = Fonts.robotoRegular(12)
    textFill = grayTextFill
    padding = Insets(2.0, 4.0, 2.0, 4.0)
  }

  init {
    val beforeLine = Pane().apply {
      background = Background(BackgroundFill(grayTextFill, CornerRadii.EMPTY, Insets.EMPTY))
      prefHeight = 1.2
      prefWidth = 16.0
    }

    val afterLine = Pane().apply {
      background = Background(BackgroundFill(grayTextFill, CornerRadii.EMPTY, Insets.EMPTY))
      prefHeight = 1.2
      prefWidth = 430.0
    }

    children.addAll(Group(beforeLine), titleLabel, Group(afterLine))
    alignment = Pos.CENTER

    prefWidth = 560.0
  }
}
