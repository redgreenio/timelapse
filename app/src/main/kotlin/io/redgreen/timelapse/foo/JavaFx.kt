package io.redgreen.timelapse.foo

import javafx.scene.layout.Region

fun Region.matchParent(parent: Region) {
  this.prefWidthProperty().bind(parent.widthProperty())
  this.prefHeightProperty().bind(parent.heightProperty())
}
