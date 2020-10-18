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
    private const val ANCHOR_STROKE_WIDTH = 1.5F

    private const val ANCHOR_STROKE_COLOR = 0x02b53d
    private const val GUIDE_STROKE_COLOR = 0xdbdbdb
    private const val CHART_FILL_COLOR = 0x665eba7d
  }

  private val guideColor = Color(GUIDE_STROKE_COLOR)
  private val anchorColor = Color(ANCHOR_STROKE_COLOR)
  private val areaChartPolygonColor = Color(CHART_FILL_COLOR, true)

  private val guideStroke = BasicStroke(GUIDE_STROKE_WIDTH)
  private val anchorStroke = BasicStroke(ANCHOR_STROKE_WIDTH)

  private var anchorIndex = 0

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

  fun setAnchorIndex(index: Int) {
    anchorIndex = index
    repaint()
  }

  private fun Graphics2D.prepareGraphics() {
    color = Color.WHITE
    fillRect(X_ORIGIN, Y_ORIGIN, width, height)

    setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
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
      color = areaChartPolygonColor
      fillPolygon(xPoints, yPoints, points.size)
    }
  }

  private fun Graphics2D.drawGuides() {
    with(this) {
      color = guideColor
      stroke = guideStroke
      xPoints.forEach { x ->
        drawLine(x, Y_ORIGIN, x, height)
      }

      color = anchorColor
      stroke = anchorStroke
      drawLine(xPoints[anchorIndex], Y_ORIGIN, xPoints[anchorIndex], height)
    }
  }
}
