package io.redgreen.timelapse.visuals

import io.redgreen.timelapse.domain.Commit
import io.redgreen.timelapse.geometry.calculateSlope

private const val X_ORIGIN = 0
private const val TOTAL_VERTICAL_PADDING_RATIO = 0.2F

@Suppress("SameParameterValue")
internal fun computePolygonPoints(
  commits: List<Commit>,
  totalWidth: Int,
  totalHeight: Int,
  pointRadius: Int,
  outPoints: MutableList<Point>
) {
  outPoints.clear()

  val horizontalSpacing = (totalWidth - pointRadius) / commits.lastIndex.toDouble()
  val graphHeight = totalHeight - (pointRadius * 2)
  val lowestValue = commits.map(Commit::insertions).minOrNull()!!
  val highestValue = commits.map(Commit::insertions).maxOrNull()!!
  val yScale = highestValue - lowestValue

  commits.onEachIndexed { index, commit ->
    val px = getX(index, horizontalSpacing)
    val py = getY(commit.insertions, graphHeight, lowestValue, yScale)

    if (index > 0) {
      val x1 = getX(index - 1, horizontalSpacing) + pointRadius
      val y1 = getY(commits[index - 1].insertions, graphHeight, lowestValue, yScale) + pointRadius
      val x2 = px + pointRadius
      val y2 = py + pointRadius

      when (index) {
        1 -> {
          val m = calculateSlope(x1, y1, x2, y2)
          val c = y1 - m * x1
          val newY1 = m * x1 + c
          val delta = if (y2 == newY1.toInt()) 0 else if (m > 0) -pointRadius else pointRadius
          outPoints.add(Point(X_ORIGIN, newY1.toInt() + delta))
        }

        commits.lastIndex -> {
          val m = calculateSlope(x1, y1, x2, y2)
          val c = y2 - m * x2
          val newY2 = m * x2 + c
          val delta = if (y1 == newY2.toInt()) 0 else if (c > 0) -pointRadius else pointRadius
          outPoints.add(Point(x1.toInt(), y1))
          outPoints.add(Point(totalWidth, newY2.toInt() + delta))
        }

        else -> outPoints.add(Point(x1.toInt(), y1))
      }
    }
  }

  outPoints.add(Point(totalWidth, totalHeight))
  outPoints.add(Point(X_ORIGIN, totalHeight))
}

private fun getX(
  index: Int,
  horizontalSpacing: Double
): Double =
  index * horizontalSpacing

private fun getY(
  additions: Int,
  graphHeight: Int,
  lowestValue: Int,
  yScale: Int
): Int {
  val totalVerticalPadding = (graphHeight * TOTAL_VERTICAL_PADDING_RATIO).toInt()
  val actualGraphHeight = graphHeight - totalVerticalPadding
  val scaledY = (additions - lowestValue).toDouble() / yScale
  return (actualGraphHeight + totalVerticalPadding / 2) - (scaledY * (actualGraphHeight)).toInt()
}
