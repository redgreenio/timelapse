package io.redgreen.visuals.demo

import io.redgreen.timelapse.visuals.AreaChart
import io.redgreen.timelapse.visuals.Commit
import io.redgreen.timelapse.visuals.RoundedCornerLabel
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.Color.LIGHTGREY
import javafx.scene.paint.Color.WHITESMOKE
import javafx.scene.text.Text
import javafx.scene.text.TextFlow

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

internal fun fileSummaryBar(): Scene {
  return Scene(VBox().apply {
    val pane = this
    background = Background(BackgroundFill(LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY))

    val fileNameLabel = Label("Filter.swift").apply {
      style = "-fx-font-weight: bold;"
      padding = Insets(0.0, 4.0, 0.0, 0.0)
    }

    children.addAll(
      HBox(
        fileNameLabel,
        RoundedCornerLabel(Color.rgb(0xff, 0xdc, 0xe0), "-5").apply { tooltip = Tooltip("5 deletions") },
        RoundedCornerLabel(Color.rgb(0xbe, 0xf5, 0xcb), "+52").apply { tooltip = Tooltip("52 insertions") },
        RoundedCornerLabel(WHITESMOKE, "1").apply { tooltip = Tooltip("1 more file changed") },
      ).apply {
        spacing = 4.0
        padding = Insets(8.0, 8.0, 0.0, 8.0)
        prefWidthProperty().bind(pane.widthProperty())
      },

      TextFlow(
        Text("e37ae8f5").apply {
          fill = Color.CORNFLOWERBLUE
          style = "-fx-font-weight: bold;"
          Tooltip.install(this, Tooltip("e37ae8f566215aa613341d17171b3b916e3f6a0a (click to copy)"))
        },
        Text(" • Make ignore element return Observable<New>")
      ).apply { padding = Insets(2.0, 8.0, 0.0, 8.0) }
    )
  }, 600.0, 52.0)
}
