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

  val greeting1Label = Label("Greeting 1")
  val greeting2Label = Label("Greeting 2")
  val greeting3Label = Label("Greeting 3")
  val greeting4Label = Label("Greeting 4")

  greetingsStackPane.children.addAll(greeting1Label, greeting2Label, greeting3Label, greeting4Label)

  val layerManager = StackPaneLayers<Greeting>(greetingsStackPane).apply {
    setLayer(ONE, greeting1Label)
    setLayer(TWO, greeting2Label)
    setLayer(THREE, greeting3Label)
    setLayer(FOUR, greeting4Label)
  }

  val button1 = Button("Button 1").apply {
    setOnAction { layerManager.show(ONE) }
  }
  val button2 = Button("Button 2").apply {
    setOnAction { layerManager.show(TWO) }
  }
  val button3 = Button("Button 3").apply {
    setOnAction { layerManager.show(THREE) }
  }
  val button4 = Button("Button 4").apply {
    setOnAction { layerManager.show(FOUR) }
  }

  val buttons = HBox(10.0, button1, button2, button3, button4).apply {
    padding = Insets(8.0)
  }

  val borderPane = BorderPane().apply {
    center = greetingsStackPane
    bottom = buttons
  }

  layerManager.show(ONE)

  return Scene(borderPane, 400.0, 400.0)
}

enum class Greeting {
  ONE, TWO, THREE, FOUR
}
