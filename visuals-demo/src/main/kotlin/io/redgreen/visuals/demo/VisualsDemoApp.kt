package io.redgreen.visuals.demo

import javafx.application.Application
import javafx.stage.Stage

class VisualsDemoApp : Application() {
  override fun init() {
    System.setProperty("prism.lcdtext", "false")
  }

  override fun start(primaryStage: Stage) {
    with(primaryStage) {
      title = "Visuals Demo"
      scene = layers()
      show()
    }
  }
}
