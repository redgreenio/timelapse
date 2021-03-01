package redgreen.dawn.extentions

import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.paint.Color

private const val CORNER_RADIUS = 1.5

fun Region.matchParent(parent: Pane) {
  this.prefWidthProperty().bind(parent.widthProperty())
  this.prefHeightProperty().bind(parent.heightProperty())
}

fun Region.backgroundFillRoundedCorners(hexColor: String) {
  this.background = Background(BackgroundFill(Color.web(hexColor), CornerRadii(CORNER_RADIUS), Insets.EMPTY))
}
