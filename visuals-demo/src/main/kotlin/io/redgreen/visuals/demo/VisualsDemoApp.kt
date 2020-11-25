package io.redgreen.visuals.demo

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.stage.Stage

class VisualsDemoApp : Application() {
  override fun start(primaryStage: Stage) {
    with(primaryStage) {
      title = "Visuals Demo"
      scene = Scene(Pane(Label("Hello, world!")), 640.0, 480.0)
      show()
    }
  }
}
