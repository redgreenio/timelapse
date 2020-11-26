package io.redgreen.timelapse.visuals.debug

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

var debug = false

private const val LINE_WIDTH = 0.5
private const val WIDER_LINE_WIDTH = 1.0
private const val GRID_SIZE_PX = 8

private val lineColor = Color.rgb(255, 178, 202)

fun GraphicsContext.drawDebugGrid(width: Double, height: Double) {
  if (!debug) return

  val columns = width.toInt() / GRID_SIZE_PX
  val rows = height.toInt() / GRID_SIZE_PX

  stroke = lineColor

  (0..columns).onEach { columnIndex ->
    lineWidth = if (columnIndex % 10 == 0) WIDER_LINE_WIDTH else LINE_WIDTH
    strokeLine(columnIndex * GRID_SIZE_PX.toDouble(), 0.0, columnIndex * GRID_SIZE_PX.toDouble(), height)
  }

  (0..rows).onEach { rowIndex ->
    lineWidth = if (rowIndex % 10 == 0) WIDER_LINE_WIDTH else LINE_WIDTH
    strokeLine(0.0, rowIndex * GRID_SIZE_PX.toDouble(), width, rowIndex * GRID_SIZE_PX.toDouble())
  }
}

fun GraphicsContext.debug(block: GraphicsContext.() -> Unit) {
  if (!debug) return
  block(this)
}
