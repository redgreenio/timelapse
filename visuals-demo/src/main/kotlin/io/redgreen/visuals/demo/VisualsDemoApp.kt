package io.redgreen.visuals.demo

import io.redgreen.timelapse.visuals.AreaChart
import io.redgreen.timelapse.visuals.Commit
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

class VisualsDemoApp : Application() {
  override fun start(primaryStage: Stage) {
    val data = listOf(6, 8, 7, 9, 7, 10).map(::Commit)

    val areaChart = AreaChart().apply {
      commits = data
    }

    val width = 800.0
    val height = 150.0

    with(primaryStage) {
      title = "Visuals Demo"

      scene = Scene(Pane().apply {
        children.add(areaChart)
        areaChart.prefWidthProperty().bind(widthProperty())
        areaChart.prefHeightProperty().bind(heightProperty())
      }, width, height)
      show()
    }
  }
}
