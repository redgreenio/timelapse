package io.redgreen.timelapse.visuals

import io.redgreen.timelapse.domain.Commit
import io.redgreen.timelapse.ui.ResizableCanvas
import io.redgreen.timelapse.visuals.debug.debug
import io.redgreen.timelapse.visuals.debug.drawDebugGrid
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.Color.WHITE
import java.awt.Point

class AreaChart : ResizableCanvas() {
  companion object {
    internal const val X_ORIGIN = 0.0
    internal const val Y_ORIGIN = 0.0

    private const val POINT_DIAMETER = 10.0
    private const val POINT_RADIUS = POINT_DIAMETER / 2

    private const val GUIDE_LINE_WIDTH = 1.0
    private const val ANCHOR_LINE_WIDTH = 2.0
  }

  private val areaChartPolygonColor = Color.rgb(94, 186, 125, 0.4)
  private val guideStrokeColor = Color.grayRgb(219)
  private val anchorStrokeColor = Color.rgb(94, 186, 125, 0.4)

  private val points = mutableListOf<Point>()
  private var xPoints = DoubleArray(0)
  private var yPoints = DoubleArray(0)
  private var anchorIndex = 0

  var commits: List<Commit> = emptyList()
    set(value) {
      field = value

      points.clear()
      xPoints = DoubleArray(0)
      yPoints = DoubleArray(0)
      invalidate()
    }

  override fun GraphicsContext.draw() {
    if (commits.isEmpty()) {
      return
    }

    prepareGraphics()

    // -- begin: Logic
    computePolygonPoints(commits, width.toInt(), height.toInt(), POINT_RADIUS.toInt(), points)
    xPoints = points.map(Point::x).map { it.toDouble() }.toDoubleArray()
    yPoints = points.map(Point::y).map { it.toDouble() }.toDoubleArray()
    // -- end: Logic

    drawChartPolygon()
    drawLines()
    drawDebugCircles()
    drawDebugGrid(width, height)
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
      fill = areaChartPolygonColor
      fillPolygon(xPoints, yPoints, points.size)
    }
  }

  private fun GraphicsContext.drawLines() {
    // guides
    stroke = guideStrokeColor
    lineWidth = GUIDE_LINE_WIDTH

    xPoints.forEach { x -> strokeLine(x, Y_ORIGIN, x, height) }

    // anchor
    stroke = anchorStrokeColor
    lineWidth = ANCHOR_LINE_WIDTH
    val anchorX = xPoints[anchorIndex].toDouble()
    strokeLine(anchorX, Y_ORIGIN, anchorX, height)
  }

  private fun GraphicsContext.drawDebugCircles() {
    debug {
      stroke = Color.RED
      lineWidth = 0.5
      points.onEach { point ->
        strokeOval(point.x - POINT_RADIUS, point.y - POINT_RADIUS, POINT_DIAMETER, POINT_DIAMETER)
      }
    }
  }
}
