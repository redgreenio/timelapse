package io.redgreen.liftoff

import io.redgreen.architecture.EntryPoint
import io.redgreen.design.DesignSystem
import io.redgreen.liftoff.javafx.DesignerGridPane
import javafx.application.Application
import javafx.geometry.Dimension2D
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.stage.Stage

abstract class Liftoff<P : Any, E> : Application() where E : EntryPoint<P>, E : Parent {
  private companion object {
    private const val drawGrid = false

    private const val PROPS_UI_WIDTH = 200.0
    private const val TITLE_SUFFIX = "[Liftoff^]"
  }

  /**
   * The feature to liftoff and (maybe) interact with.
   */
  protected abstract val entryPoint: E

  /**
   * Props for the feature's [EntryPoint].
   */
  protected abstract val props: P

  /**
   * UI in the liftoff window to manipulate the props.
   */
  protected abstract val propsUi: Region

  /**
   * The title of this feature.
   */
  protected abstract val title: String

  /**
   * Size of the feature in the liftoff window.
   */
  protected abstract val howBig: Dimension2D

  override fun start(primaryStage: Stage) {
    val size = howBig
    val root = HBox()
    val content = if (drawGrid) DesignerGridPane() else Pane()

    val scenePane = Scene(root, size.width + PROPS_UI_WIDTH, size.height).apply {
      DesignSystem.initialize(this)
    }

    entryPoint.mount(props)
    println("${entryPoint::class.java.simpleName} mounted.")

    propsUi.apply {
      padding = Insets(8.0)
      prefWidth(PROPS_UI_WIDTH)
    }

    root.children.addAll(
      content.apply { prefWidth = size.width },
      propsUi
    )

    if (content is DesignerGridPane) {
      content.setOnlyChild(entryPoint as Region)
    } else {
      content.children.add(entryPoint)
      (entryPoint as Region).prefWidthProperty().bind(content.widthProperty())
      (entryPoint as Region).prefHeightProperty().bind(content.heightProperty())
    }

    with(primaryStage) {
      title = "${title} $TITLE_SUFFIX"
      scene = scenePane
      centerOnScreen()
      show()
    }
  }

  override fun init() {
    System.setProperty("prism.lcdtext", "false")
  }

  override fun stop() {
    entryPoint.unmount()
    println("${entryPoint::class.java.simpleName} unmounted.")
  }
}
