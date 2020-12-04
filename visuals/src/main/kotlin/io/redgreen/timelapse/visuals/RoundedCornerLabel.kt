package io.redgreen.timelapse.visuals

import javafx.geometry.Insets
import javafx.geometry.Insets.EMPTY
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color

class RoundedCornerLabel(
  backgroundColor: Color
): Label() {
  companion object {
    private const val MIN_WIDTH = 24.0
    private const val CORNER_RADII = 3.5
    private const val PADDING = 4.0
  }

  init {
    minWidth = MIN_WIDTH
    alignment = Pos.CENTER

    val cornerRadii = CornerRadii(CORNER_RADII)
    background = Background(BackgroundFill(backgroundColor, cornerRadii, EMPTY))
    padding = Insets(0.0, PADDING, 0.0, PADDING)
  }
}
