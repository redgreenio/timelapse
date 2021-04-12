package io.redgreen.timelapse.workbench.menu.javafx

import io.redgreen.timelapse.foo.closeWindow
import io.redgreen.timelapse.main.TimelapseScene
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.stage.Stage

class OpenRecentRepositoryEventHandler(
  private val scene: Scene,
  private val repositoryDirectoryPath: String
) : EventHandler<ActionEvent> {
  override fun handle(event: ActionEvent) {
    scene.closeWindow()
    TimelapseScene.launch(Stage(), repositoryDirectoryPath)
  }
}
