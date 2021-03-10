package io.redgreen.visuals.demo

import io.redgreen.timelapse.visuals.StackPaneLayers
import io.redgreen.visuals.demo.Greeting.FOUR
import io.redgreen.visuals.demo.Greeting.ONE
import io.redgreen.visuals.demo.Greeting.THREE
import io.redgreen.visuals.demo.Greeting.TWO
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane

@Suppress("unused")
internal fun layersScene(): Scene {
  val greetingsStackPane = StackPane()

  val greetingLayers = StackPaneLayers<Greeting>(greetingsStackPane) {
    Label("Greeting 1") at ONE
    Label("Greeting 2") at TWO
    Label("Greeting 3") at THREE
    Label("Greeting 4") at FOUR
  }

  val button1 = Button("Button 1").apply {
    setOnAction { greetingLayers.show(ONE) }
  }
  val button2 = Button("Button 2").apply {
    setOnAction { greetingLayers.show(TWO) }
  }
  val button3 = Button("Button 3").apply {
    setOnAction { greetingLayers.show(THREE) }
  }
  val button4 = Button("Button 4").apply {
    setOnAction { greetingLayers.show(FOUR) }
  }

  val buttons = HBox(10.0, button1, button2, button3, button4).apply {
    padding = Insets(8.0)
  }

  val borderPane = BorderPane().apply {
    center = greetingsStackPane
    bottom = buttons
  }

  greetingLayers.show(ONE)

  return Scene(borderPane, 400.0, 400.0)
}

enum class Greeting {
  ONE, TWO, THREE, FOUR
}
