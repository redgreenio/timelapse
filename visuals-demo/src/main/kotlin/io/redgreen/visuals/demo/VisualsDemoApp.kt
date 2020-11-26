package io.redgreen.visuals.demo

import io.redgreen.timelapse.visuals.AreaChart
import io.redgreen.timelapse.visuals.Commit
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

class VisualsDemoApp : Application() {
  override fun start(primaryStage: Stage) {
    val sampleCommits = listOf(-3 ,-2, -1, 0, 1, 2, 3).map { Commit(it, 0) }

    val areaChart = AreaChart().apply {
      commits = sampleCommits
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
