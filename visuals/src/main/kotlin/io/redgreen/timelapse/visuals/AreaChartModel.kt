package io.redgreen.timelapse.visuals

import io.redgreen.timelapse.geometry.calculateSlope

private const val X_ORIGIN = 0
private const val TOTAL_VERTICAL_PADDING_FRACTION = 0.2

@Suppress("SameParameterValue")
internal fun computePolygonPoints(
  commits: List<Commit>,
  viewportWidth: Int,
  viewportHeight: Int,
  outPoints: MutableList<Point>,
  verticalPaddingFraction: Double = TOTAL_VERTICAL_PADDING_FRACTION,
) {
  outPoints.clear()

  val horizontalSpacing = viewportWidth / commits.lastIndex.toDouble()
  val lowestValue = commits.map(Commit::insertions).minOrNull()!!
  val highestValue = commits.map(Commit::insertions).maxOrNull()!!
  val yScale = highestValue - lowestValue

  commits.onEachIndexed { index, commit ->
    val px = getX(index, horizontalSpacing)
    val py = getY(commit.insertions, viewportHeight, lowestValue, yScale, verticalPaddingFraction)

    if (index > 0) {
      val x1 = getX(index - 1, horizontalSpacing)
      val y1 = getY(commits[index - 1].insertions, viewportHeight, lowestValue, yScale, verticalPaddingFraction)
      val x2 = px
      val y2 = py
      val m = calculateSlope(x1, y1, x2, y2)

      when (index) {
        1 -> {
          val c = y1 - m * x1
          val newY1 = m * x1 + c
          outPoints.add(Point(X_ORIGIN, newY1.toInt()))
        }

        commits.lastIndex -> {
          val c = y2 - m * x2
          val newY2 = m * x2 + c
          outPoints.add(Point(x1.toInt(), y1))
          outPoints.add(Point(viewportWidth, newY2.toInt()))
        }

        else -> outPoints.add(Point(x1.toInt(), y1))
      }
    }
  }

  outPoints.add(Point(viewportWidth, viewportHeight))
  outPoints.add(Point(X_ORIGIN, viewportHeight))
}

private fun getX(
  index: Int,
  horizontalSpacing: Double
): Double =
  index * horizontalSpacing

private fun getY(
  insertions: Int,
  graphHeight: Int,
  lowestValue: Int,
  yScale: Int,
  verticalPaddingFraction: Double
): Int {
  val totalVerticalPadding = (graphHeight * verticalPaddingFraction).toInt()
  val actualGraphHeight = graphHeight - totalVerticalPadding
  val scaledY = (insertions - lowestValue).toDouble() / yScale
  return (actualGraphHeight + totalVerticalPadding / 2) - (scaledY * (actualGraphHeight)).toInt()
}
