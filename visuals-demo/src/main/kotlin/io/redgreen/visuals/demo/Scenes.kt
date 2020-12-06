package io.redgreen.visuals.demo

import io.redgreen.timelapse.visuals.AreaChart
import io.redgreen.timelapse.visuals.Commit
import javafx.scene.Scene
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.scene.control.Tooltip
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

    val tab1Content = Region().apply { style = "-fx-background-color: white;" }
    val tab1 = Tab("build.gradle", tab1Content).apply { isClosable = false }.apply {
      tooltip = Tooltip("build: update library dependencies (2 weeks ago)")
    }

    val tab2Content = Region().apply { style = "-fx-background-color: white;" }
    val tab2 = Tab("build.gradle @ a73ef1ae", tab2Content).apply {
      tooltip = Tooltip("style: format buildscripts according to the latest style guide (1 hour ago)")
    }

    val tab3Content = Region().apply { style = "-fx-background-color: white;" }
    val tab3 = Tab("build.gradle @ 33d1a90b", tab3Content).apply {
      tooltip = Tooltip("ci: send out alerts via Slack for failed quality checks (9 months ago)")
    }

    val tab4Content = Region().apply { style = "-fx-background-color: white;" }
    val tab4 = Tab("build.gradle @ f85b284b", tab4Content).apply {
      tooltip = Tooltip("chore: initial commit (2 years ago)")
    }

    tabPane.tabs.addAll(tab1, tab2, tab3, tab4)

    children.add(tabPane)
  }, 640.0, 480.0)
}
