package toys.lliftoff

import io.redgreen.design.DesignSystem
import io.redgreen.timelapse.foo.fastLazy
import javafx.application.Application
import javafx.geometry.Dimension2D
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.stage.Stage
import redgreen.dawn.architecture.EntryPoint

abstract class LiftOff<P : Any, E> : Application() where E : EntryPoint<P>, E : Parent {
  private companion object {
    private const val PROPS_UI_WIDTH = 200.0
  }

  private val debug = false
  private val entryPoint: E by fastLazy { entryPoint() }

  override fun start(primaryStage: Stage) {
    val size = howBig()
    val root = HBox()
    val content = if (debug) MaterialGridPane() else Pane()

    val scenePane = Scene(root, size.width + PROPS_UI_WIDTH, size.height).apply {
      DesignSystem.initialize(this)
    }

    entryPoint.mount(props())
    println("${entryPoint::class.java.simpleName} mounted.")

    val propsUi = propsUi().apply {
      padding = Insets(8.0)
      prefWidth(PROPS_UI_WIDTH)
    }

    root.children.addAll(
      content.apply { prefWidth = size.width },
      propsUi
    )

    if (content is MaterialGridPane) {
      content.setOnlyChild(entryPoint as Region)
    } else {
      content.children.add(entryPoint)
      (entryPoint as Region).prefWidthProperty().bind(content.widthProperty())
      (entryPoint as Region).prefHeightProperty().bind(content.heightProperty())
    }

    with(primaryStage) {
      title = "${title()} [Liftoff^]"
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

  abstract fun title(): String
  abstract fun howBig(): Dimension2D
  abstract fun entryPoint(): E
  abstract fun props(): P
  abstract fun propsUi(): Region
}
