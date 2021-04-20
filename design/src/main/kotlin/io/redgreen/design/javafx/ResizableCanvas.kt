package io.redgreen.design.javafx

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Pane

abstract class ResizableCanvas : Pane() {
  private val canvas = object : Canvas() {
    override fun isResizable(): Boolean {
      return true
    }
  }

  init {
    children.add(canvas)
    with(canvas) {
      widthProperty().bind(this@ResizableCanvas.widthProperty())
      heightProperty().bind(this@ResizableCanvas.heightProperty())

      widthProperty().addListener { _ -> draw() }
      heightProperty().addListener { _ -> draw() }
    }
  }

  abstract fun GraphicsContext.draw()

  protected fun invalidate() {
    draw()
  }

  private fun draw() {
    canvas.graphicsContext2D.draw()
  }
}
