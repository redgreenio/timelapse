package io.redgreen.timelapse.visuals

import io.redgreen.timelapse.geometry.calculateSlope
import kotlin.math.max
import kotlin.math.min

private const val X_ORIGIN = 0
private const val TOTAL_VERTICAL_PADDING_FRACTION = 0.2

@Suppress("SameParameterValue")
internal fun computePolygonPoints(
  commits: List<Commit>,
  viewportWidth: Int,
  viewportHeight: Int,
  outInsertionPoints: MutableList<Point>,
  outDeletionPoints: MutableList<Point>,
  verticalPaddingFraction: Double = TOTAL_VERTICAL_PADDING_FRACTION,
) {
  outInsertionPoints.clear()
  outDeletionPoints.clear()
  val distanceBetweenValues = viewportWidth / commits.lastIndex.toDouble()

  val insertionsAndDeletions = commits.map { it.insertions + it.deletions }

  val lowestDeletionValue = insertionsAndDeletions.minOrNull()!!
  val highestDeletionValue = insertionsAndDeletions.maxOrNull()!!
  val deletionsYScale = highestDeletionValue - lowestDeletionValue
  computePolygon(
    insertionsAndDeletions,
    viewportWidth,
    viewportHeight,
    lowestDeletionValue,
    deletionsYScale,
    distanceBetweenValues,
    verticalPaddingFraction,
    outDeletionPoints
  )

  val lowestInsertionValue = commits.map(Commit::insertions).minOrNull()!!
  val highestInsertionValue = commits.map(Commit::insertions).maxOrNull()!!
  val insertionsYScale = max(highestInsertionValue, highestDeletionValue) - min(lowestInsertionValue, lowestDeletionValue)

  computePolygon(
    commits.map(Commit::insertions),
    viewportWidth,
    viewportHeight,
    lowestInsertionValue,
    insertionsYScale,
    distanceBetweenValues,
    verticalPaddingFraction,
    outInsertionPoints
  )
}

private fun computePolygon(
  values: List<Int>,
  viewportWidth: Int,
  viewportHeight: Int,
  lowestValue: Int,
  yScale: Int,
  distanceBetweenValues: Double,
  verticalPaddingFraction: Double,
  outPoints: MutableList<Point>,
) {
  values.onEachIndexed { index, value ->
    val px = getX(index, distanceBetweenValues)
    val py = getY(value, viewportHeight, lowestValue, yScale, verticalPaddingFraction)

    if (index > 0) {
      val x1 = getX(index - 1, distanceBetweenValues)
      val y1 = getY(values[index - 1], viewportHeight, lowestValue, yScale, verticalPaddingFraction)
      val x2 = px
      val y2 = py
      val m = calculateSlope(x1, y1, x2, y2)

      when (index) {
        1 -> {
          val c = y1 - m * x1
          val newY1 = m * x1 + c
          outPoints.add(Point(X_ORIGIN, newY1.toInt()))
        }

        values.lastIndex -> {
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
