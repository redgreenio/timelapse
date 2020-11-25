package io.redgreen.visuals.demo

import io.redgreen.timelapse.visuals.AreaChart
import io.redgreen.timelapse.visuals.Commit
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

class VisualsDemoApp : Application() {
  override fun start(primaryStage: Stage) {
    val sampleCommits = listOf(
      Commit(0, 0), // 0
      Commit(0, 1), // 1
      Commit(1, 1), // 2
      Commit(2, 1), // 3
      Commit(2, 2), // 4
      Commit(2, 3), // 5
    )

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
