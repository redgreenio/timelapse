package io.redgreen.timelapse.visuals.debug

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON

var debug = true

private const val STROKE_WIDTH = 0.5F
private const val WIDER_STROKE_WIDTH = 1.0F
private const val STROKE_COLOR = 0xffb2ca
private const val GRID_SIZE_PX = 8

private val regularStroke = BasicStroke(STROKE_WIDTH)
private val widerStroke = BasicStroke(WIDER_STROKE_WIDTH)

fun Graphics2D.drawDebugGrid(width: Int, height: Int) {
  if (!debug) return

  val columns = width / GRID_SIZE_PX
  val rows = height / GRID_SIZE_PX

  setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
  color = Color(STROKE_COLOR)

  (0..columns).onEach { columnIndex ->
    stroke = if (columnIndex % 10 == 0) widerStroke else regularStroke
    drawLine(columnIndex * GRID_SIZE_PX, 0, columnIndex * GRID_SIZE_PX, height)
  }

  (0..rows).onEach { rowIndex ->
    stroke = if (rowIndex % 10 == 0) widerStroke else regularStroke
    drawLine(0, rowIndex * GRID_SIZE_PX, width, rowIndex * GRID_SIZE_PX)
  }
}

fun Graphics2D.debug(block: Graphics2D.() -> Unit) {
  if (!debug) return
  block(this)
}
