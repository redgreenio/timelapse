package io.redgreen.liftoff.javafx

import io.redgreen.design.ResizableCanvas
import io.redgreen.liftoff.javafx.extensions.drawDesignerGrid
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import kotlin.LazyThreadSafetyMode.NONE

class DesignerGridPane : Pane() {
  private val designerGridCanvas by lazy(NONE) {
    DesignerGridCanvas()
  }

  fun setOnlyChild(node: Region) {
    node.matchParent(this)
    designerGridCanvas.matchParent(this)

    with(children) {
      clear()
      add(node)
      add(designerGridCanvas)
    }
  }

  private fun Region.matchParent(parent: Region) {
    prefWidthProperty().bind(parent.widthProperty())
    prefHeightProperty().bind(parent.heightProperty())
  }
}

private class DesignerGridCanvas : ResizableCanvas() {
  private companion object {
    private const val GRID_COLOR = "#C2185BFA"
    private const val GRID_SIZE = 8
  }

  init {
    isMouseTransparent = true
  }

  override fun GraphicsContext.draw() {
    drawDesignerGrid(width, height, GRID_SIZE, Color.web(GRID_COLOR))
  }
}
