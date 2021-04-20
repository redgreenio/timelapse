package io.redgreen.visuals.demo

import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioMenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.ToggleGroup
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Region

internal fun themesScene(): Scene {
  val root = BorderPane()

  val fileMenu = Menu("File")

  val menuBar = MenuBar().apply {
    val openMenuItem = MenuItem("Open...")

    val lightTheme = RadioMenuItem("Light").apply {
      setOnAction { switchToLightTheme(root) }
    }
    val darkTheme = RadioMenuItem("Dark").apply {
      setOnAction { switchToDarkTheme(root) }
    }

    ToggleGroup().apply { toggles.addAll(lightTheme, darkTheme) }

    val themesSubmenu = Menu("Themes").apply {
      items.addAll(lightTheme, darkTheme)
    }

    fileMenu.items.addAll(openMenuItem, themesSubmenu)

    menus.addAll(fileMenu)
  }

  val scene = Scene(root, 640.0, 480.0)

  root.apply {
    val tabPane = TabPane().apply {
      prefWidthProperty().bind(root.widthProperty())
      prefHeightProperty().bind(root.heightProperty())
      tabClosingPolicy = TabPane.TabClosingPolicy.ALL_TABS

      style = """
        -fx-open-tab-animation: none;
        -fx-close-tab-animation: none;
      """.trimIndent()
    }

    addTabs(tabPane)

    top = menuBar
    center = tabPane
  }

  return scene
}

private fun switchToDarkTheme(root: Pane) {
  switchTheme(root.scene, "css/dark-colors.css")
}

private fun switchToLightTheme(root: Pane) {
  switchTheme(root.scene, "css/light-colors.css")
}

private fun switchTheme(scene: Scene, cssFileName: String) {
  with(scene) {
    stylesheets.clear()
    userAgentStylesheet = null
    stylesheets.addAll(cssFileName, "css/controls.css")
  }
}

private fun addTabs(tabPane: TabPane) {
  val tab1Content = Region().apply { style = "-fx-background-color: -fx-theme-base;" }
  val tab1 = Tab("build.gradle", tab1Content).apply { isClosable = false }.apply {
    tooltip = Tooltip("build: update library dependencies (2 weeks ago)")
  }

  val tab2Content = Region().apply { style = "-fx-background-color: -fx-theme-base;" }
  val tab2 = Tab("build.gradle @ a73ef1ae", tab2Content).apply {
    tooltip = Tooltip("style: format buildscripts according to the latest style guide (1 hour ago)")
  }

  val tab3Content = Region().apply { style = "-fx-background-color: -fx-theme-base;" }
  val tab3 = Tab("build.gradle @ 33d1a90b", tab3Content).apply {
    tooltip = Tooltip("ci: send out alerts via Slack for failed quality checks (9 months ago)")
  }

  val tab4Content = Region().apply { style = "-fx-background-color: -fx-theme-base;" }
  val tab4 = Tab("build.gradle @ f85b284b", tab4Content).apply {
    tooltip = Tooltip("chore: initial commit (2 years ago)")
  }

  tabPane.tabs.addAll(tab1, tab2, tab3, tab4)
}
