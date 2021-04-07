package io.redgreen.timelapse.foo

import javafx.application.Platform
import javafx.scene.layout.Region
import javafx.stage.Stage
import kotlin.system.exitProcess

fun Region.matchParent(parent: Region) {
  this.prefWidthProperty().bind(parent.widthProperty())
  this.prefHeightProperty().bind(parent.heightProperty())
}

fun Stage.exitOnClose(): Stage {
  setOnCloseRequest {
    Platform.exit()
    exitProcess(0)
  }
  return this
}
