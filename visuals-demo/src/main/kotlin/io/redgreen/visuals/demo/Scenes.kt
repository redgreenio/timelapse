package io.redgreen.visuals.demo

import io.redgreen.timelapse.visuals.AreaChart
import io.redgreen.timelapse.visuals.Commit
import javafx.scene.Scene
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.scene.layout.Pane
import javafx.scene.layout.Region

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

internal fun tabsScene(): Scene {
  return Scene(Pane().apply {
    val root = this
    stylesheets.add("css/tabs.css")

    val tabPane = TabPane().apply {
      prefWidthProperty().bind(root.widthProperty())
      prefHeightProperty().bind(root.heightProperty())
      tabClosingPolicy = TabClosingPolicy.ALL_TABS

      style = """
        -fx-open-tab-animation: none;
        -fx-close-tab-animation: none;
      """.trimIndent()
    }

    val firstTab = Tab("build.gradle", Region().apply { style = "-fx-background-color: white;" })
      .apply { isClosable = false }

    val secondTab = Tab("build.gradle @ a73ef1ae", Region().apply { style = "-fx-background-color: white;" })
    val thirdTab = Tab("build.gradle @ 33d1a90b", Region().apply { style = "-fx-background-color: white;" })
    val fourthTab = Tab("build.gradle @ f85b284b", Region().apply { style = "-fx-background-color: white;" })

    tabPane.tabs.addAll(firstTab, secondTab, thirdTab, fourthTab)

    children.add(tabPane)
  }, 640.0, 480.0)
}
