package redgreen.dawn.extentions

import javafx.scene.layout.Pane
import javafx.scene.layout.Region

fun Region.matchParent(parent: Pane) {
  this.prefWidthProperty().bind(parent.widthProperty())
  this.prefHeightProperty().bind(parent.heightProperty())
}
