package toys.lliftoff

import io.redgreen.timelapse.foo.fastLazy
import javafx.application.Application
import javafx.geometry.Dimension2D
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.stage.Stage
import redgreen.dawn.architecture.EntryPoint

abstract class LiftOff<P : Any, E> : Application() where E : EntryPoint<P>, E : Parent {
  private val debug = false
  private val entryPoint: E by fastLazy { entryPoint() }

  override fun start(primaryStage: Stage) {
    val size = howBig()
    val root = if (debug) MaterialGridPane() else Pane()
    val scenePane = Scene(root, size.width, size.height).apply {
      loadCssFiles(this)
    }

    entryPoint.mount(props())
    println("${entryPoint::class.java.simpleName} mounted.")

    if (root is MaterialGridPane) {
      root.setOnlyChild(entryPoint as Region)
    } else {
      root.children.add(entryPoint)
      (entryPoint as Region).prefWidthProperty().bind(root.widthProperty())
      (entryPoint as Region).prefHeightProperty().bind(root.heightProperty())
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

  private fun loadCssFiles(scene: Scene) {
    val cssUri = LiftOff::class.java.getResource("/css/fonts.css").toExternalForm()
    scene.stylesheets.add(cssUri)
  }
}
