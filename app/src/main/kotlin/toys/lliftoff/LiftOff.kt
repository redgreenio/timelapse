package toys.lliftoff

import javafx.application.Application
import javafx.geometry.Dimension2D
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import redgreen.dawn.architecture.EntryPoint

abstract class LiftOff<P : Any, E> : Application() where E : EntryPoint<P>, E : Parent {
  private lateinit var entryPoint: E

  override fun start(primaryStage: Stage) {
    entryPoint = entryPoint()
    entryPoint.mount(props())
    println("${entryPoint::class.java.simpleName} mounted.")

    val size = howBig()
    val scenePane = Scene(entryPoint, size.width, size.height).apply {
      loadCssFiles(this)
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
