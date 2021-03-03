package io.redgreen.timelapse.visuals

import io.redgreen.design.ResizableCanvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.Color.WHITE

class AreaChart : ResizableCanvas() {
  companion object {
    internal const val X_ORIGIN = 0.0
    internal const val Y_ORIGIN = 0.0

    private const val POINT_DIAMETER = 10.0
    private const val POINT_RADIUS = POINT_DIAMETER / 2

    private const val GUIDE_LINE_WIDTH = 1.0
    private const val ANCHOR_LINE_WIDTH = 2.0

    private const val debug = false
  }

  private val insertionsPolygonColor = Color.rgb(198, 240, 194)
  private val deletionsPolygonColor = Color.rgb(240, 194, 194)
  private val guideStrokeColor = Color.grayRgb(219)
  private val anchorStrokeColor = Color.rgb(94, 186, 125, 0.4)

  private val insertionPoints = mutableListOf<Point>()
  private var xInsertionPoints = DoubleArray(0)
  private var yInsertionPoints = DoubleArray(0)

  private val deletionPoints = mutableListOf<Point>()
  private var xDeletionPoints = DoubleArray(0)
  private var yDeletionPoints = DoubleArray(0)

  private var anchorIndex = 0

  var commits: List<Commit> = emptyList()
    set(value) {
      field = value

      insertionPoints.clear()
      xInsertionPoints = DoubleArray(0)
      yInsertionPoints = DoubleArray(0)

      deletionPoints.clear()
      xDeletionPoints = DoubleArray(0)
      yDeletionPoints = DoubleArray(0)

      anchorIndex = 0
      invalidate()
    }

  override fun GraphicsContext.draw() {
    if (commits.isEmpty()) {
      return
    }

    prepareGraphics()

    // -- begin: Logic
    computePolygonPoints(commits, width.toInt(), height.toInt(), insertionPoints, deletionPoints)
    xInsertionPoints = insertionPoints.map(Point::x).map { it.toDouble() }.toDoubleArray()
    yInsertionPoints = insertionPoints.map(Point::y).map { it.toDouble() }.toDoubleArray()

    xDeletionPoints = deletionPoints.map(Point::x).map { it.toDouble() }.toDoubleArray()
    yDeletionPoints = deletionPoints.map(Point::y).map { it.toDouble() }.toDoubleArray()
    // -- end: Logic

    drawChartPolygon()
    drawLines()
    drawDebugCircles()
  }

  fun setAnchorIndex(index: Int) {
    anchorIndex = index
    invalidate()
  }

  private fun GraphicsContext.prepareGraphics() {
    fill = WHITE
    fillRect(X_ORIGIN, Y_ORIGIN, width, height)
  }

  private fun GraphicsContext.drawChartPolygon() {
    with(this) {
      fill = deletionsPolygonColor
      fillPolygon(xDeletionPoints, yDeletionPoints, deletionPoints.size)
    }

    with(this) {
      fill = insertionsPolygonColor
      fillPolygon(xInsertionPoints, yInsertionPoints, insertionPoints.size)
    }
  }

  private fun GraphicsContext.drawLines() {
    if (commits.size == 1) return

    // guides
    stroke = guideStrokeColor
    lineWidth = GUIDE_LINE_WIDTH

    xInsertionPoints.forEach { x -> strokeLine(x, Y_ORIGIN, x, height) }

    // anchor
    stroke = anchorStrokeColor
    lineWidth = ANCHOR_LINE_WIDTH
    val anchorX = xInsertionPoints[anchorIndex]
    strokeLine(anchorX, Y_ORIGIN, anchorX, height)
  }

  private fun GraphicsContext.drawDebugCircles() {
    if (debug) {
      stroke = Color.RED
      lineWidth = 0.5
      insertionPoints.onEach { point ->
        strokeOval(point.x - POINT_RADIUS, point.y - POINT_RADIUS, POINT_DIAMETER, POINT_DIAMETER)
      }
    }
  }
}
