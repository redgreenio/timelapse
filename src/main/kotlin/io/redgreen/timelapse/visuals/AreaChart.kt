package io.redgreen.timelapse.visuals

import io.redgreen.timelapse.domain.Commit
import io.redgreen.timelapse.visuals.debug.debug
import io.redgreen.timelapse.visuals.debug.drawDebugGrid
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON
import javax.swing.JPanel

class AreaChart : JPanel() {
  companion object {
    internal const val X_ORIGIN = 0
    internal const val Y_ORIGIN = 0
    internal const val TOTAL_VERTICAL_PADDING_RATIO = 0.2F

    private const val POINT_DIAMETER = 10
    private const val POINT_RADIUS = POINT_DIAMETER / 2

    private const val GUIDE_STROKE_WIDTH = 1.0F
    private const val STROKE_COLOR = 0x02b53d
    private const val FILL_COLOR = 0x665eba7d
  }

  private var anchorX = 0

  private val points = mutableListOf<Point>()
  private var xPoints = IntArray(0)
  private var yPoints = IntArray(0)

  private var knownWidth = 0
  private var knownHeight = 0

  var commits: List<Commit> = emptyList()
    set(value) {
      field = value

      points.clear()
      xPoints = IntArray(0)
      yPoints = IntArray(0)

      repaint()
    }

  override fun paintComponent(graphics: Graphics) {
    if (commits.isEmpty()) {
      return
    }

    (graphics as Graphics2D).apply { prepareGraphics() }

    // -- begin: Logic
    computePolygonPoints(commits, width, height, POINT_RADIUS, points)
    xPoints = points.map(Point::x).toIntArray()
    yPoints = points.map(Point::y).toIntArray()
    knownWidth = width
    knownHeight = height
    // -- end: Logic

    with(graphics) {
      drawDebugCircles()
      drawChartPolygon()
      drawGuides()
      drawDebugGrid(width, height)
    }
  }

  private fun Graphics2D.prepareGraphics() {
    color = Color.WHITE
    fillRect(0, 0, width, height)

    setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
    stroke = BasicStroke(GUIDE_STROKE_WIDTH)
    color = Color(STROKE_COLOR)
  }

  private fun Graphics2D.drawDebugCircles() {
    this.debug {
      points.onEach {
        drawOval(it.x - POINT_RADIUS, it.y - POINT_RADIUS, POINT_DIAMETER, POINT_DIAMETER)
      }
    }
  }

  private fun Graphics2D.drawChartPolygon() {
    with(this) {
      color = Color(FILL_COLOR, true)
      fillPolygon(xPoints, yPoints, points.size)

      color = Color(STROKE_COLOR)
      // drawPolygon(x, y, points.size) // Hold off on drawing borders for the polygon
      drawLine(anchorX, Y_ORIGIN, anchorX, height)
    }
  }

  private fun Graphics2D.drawGuides() {
    with(this) {
      color = Color.LIGHT_GRAY
      xPoints.forEachIndexed { index, x ->
        drawLine(x, 0, x, height)
      }
    }
  }
}
