package io.redgreen.timelapse.foo

import io.redgreen.timelapse.APP_NAME
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.layout.Region
import javafx.stage.Stage
import kotlin.system.exitProcess

fun Region.matchParent(parent: Region) {
  this.prefWidthProperty().bind(parent.widthProperty())
  this.prefHeightProperty().bind(parent.heightProperty())
}

fun Stage.exitOnClose(beforeExitAction: () -> Unit = {}): Stage {
  setOnCloseRequest {
    beforeExitAction()
    Platform.exit()
    exitProcess(0)
  }
  return this
}

fun launchScene(
  stage: Stage,
  sceneToShow: Scene,
  resizable: Boolean
) {
  with(stage) {
    scene = sceneToShow
    isResizable = resizable
    title = APP_NAME
    centerOnScreen()
    show()
  }
}

fun Scene.closeWindow() {
  (window as Stage).close()
}
