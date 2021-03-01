package redgreen.dawn.extentions

import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Region
import javafx.scene.paint.Color

private const val CORNER_RADIUS = 1.5

fun Region.matchParent(parent: Region) {
  this.prefWidthProperty().bind(parent.widthProperty())
  this.prefHeightProperty().bind(parent.heightProperty())
}

fun Region.backgroundFillRoundedCorners(hexColor: String, cornerRadius: Double = CORNER_RADIUS) {
  this.background = Background(BackgroundFill(Color.web(hexColor), CornerRadii(cornerRadius), Insets.EMPTY))
}

fun Region.backgroundFill(hexColor: String) {
  backgroundFillRoundedCorners(hexColor, 0.0)
}
