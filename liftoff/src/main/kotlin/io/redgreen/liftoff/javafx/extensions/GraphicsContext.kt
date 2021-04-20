package io.redgreen.liftoff.javafx.extensions

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

private const val LINE_WIDTH = 0.1
private const val WIDER_LINE_WIDTH = 0.3
private const val GRID_SIZE_PX = 8

private val lineColor = Color.rgb(255, 178, 202)

fun GraphicsContext.drawDesignerGrid(
  width: Double,
  height: Double,
  gridSize: Int = GRID_SIZE_PX,
  gridColor: Color = lineColor,
) {
  clearRect(0.0, 0.0, width, height)

  val columns = width.toInt() / gridSize
  val rows = height.toInt() / gridSize

  stroke = gridColor

  (0..columns).onEach { columnIndex ->
    lineWidth = if (columnIndex % 10 == 0) WIDER_LINE_WIDTH else LINE_WIDTH
    strokeLine(columnIndex * gridSize.toDouble(), 0.0, columnIndex * gridSize.toDouble(), height)
  }

  (0..rows).onEach { rowIndex ->
    lineWidth = if (rowIndex % 10 == 0) WIDER_LINE_WIDTH else LINE_WIDTH
    strokeLine(0.0, rowIndex * gridSize.toDouble(), width, rowIndex * gridSize.toDouble())
  }
}
