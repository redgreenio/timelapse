package io.redgreen.visuals.demo

import io.redgreen.timelapse.visuals.AreaChart
import io.redgreen.timelapse.visuals.Commit
import javafx.scene.Scene
import javafx.scene.layout.Pane

private const val areaChartWidth = 800
private const val areaChartHeight = 150

private val sampleCommits = listOf(
  Commit(5, 0),
  Commit(1, 0),
  Commit(1, 4),
  Commit(2, 0),
  Commit(0, 1),
  Commit(1, 0),
  Commit(1, 0),
)

private val areaChart = AreaChart().apply {
  commits = sampleCommits
}

@Suppress("unused")
internal fun areaChartScene(): Scene {
  return Scene(Pane().apply {
    children.add(areaChart)
    areaChart.prefWidthProperty().bind(widthProperty())
    areaChart.prefHeightProperty().bind(heightProperty())
  }, areaChartWidth.toDouble(), areaChartHeight.toDouble())
}
