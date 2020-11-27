package io.redgreen.timelapse.visuals

import io.redgreen.timelapse.geometry.calculateSlope
import kotlin.math.max
import kotlin.math.min

private const val X_ORIGIN = 0
private const val TOTAL_VERTICAL_PADDING_FRACTION = 0.0

@Suppress("SameParameterValue")
internal fun computePolygonPoints(
  commits: List<Commit>,
  viewportWidth: Int,
  viewportHeight: Int,
  outInsertionPoints: MutableList<Point>,
  outDeletionPoints: MutableList<Point>,
) {
  outInsertionPoints.clear()
  outDeletionPoints.clear()
  val isSingleCommit = commits.size == 1
  val sanitizedCommits = if (isSingleCommit) commits.flatMap { listOf(it, it, it) } else commits
  val xAxisDistanceBetweenValues = viewportWidth / sanitizedCommits.lastIndex.toDouble()

  val insertions = sanitizedCommits.map(Commit::insertions)
  val deletions = sanitizedCommits.map(Commit::deletions)
  val maximum = sanitizedCommits.map { it.insertions + it.deletions }.maxOrNull()!!
  val minimum = calculateMinimum(insertions, deletions, isSingleCommit)
  val deletionsYScale = maximum - minimum

  val insertionsAndDeletions = sanitizedCommits.map { it.insertions + it.deletions }

  computePolygon(
    insertionsAndDeletions,
    viewportWidth,
    viewportHeight,
    minimum,
    deletionsYScale,
    xAxisDistanceBetweenValues,
    outDeletionPoints
  )

  computePolygon(
    insertions,
    viewportWidth,
    viewportHeight,
    minimum,
    deletionsYScale,
    xAxisDistanceBetweenValues,
    outInsertionPoints
  )
}

private fun calculateMinimum(
  insertions: List<Int>,
  deletions: List<Int>,
  isSingleCommit: Boolean
): Int {
  return if (isSingleCommit) {
    0
  } else {
    val minimumThreshold = (max(insertions.minOrNull()!!, deletions.minOrNull()!!) - 1).coerceAtLeast(0)
    min(insertions.minOrNull()!!, deletions.minOrNull()!!).coerceAtLeast(minimumThreshold)
  }
}

private fun computePolygon(
  values: List<Int>,
  viewportWidth: Int,
  viewportHeight: Int,
  minimumValue: Int,
  yScale: Int,
  xAxisDistanceBetweenValues: Double,
  outPoints: MutableList<Point>,
) {
  values.onEachIndexed { index, value ->
    val px = getX(index, xAxisDistanceBetweenValues)
    val py = getY(value, viewportHeight, minimumValue, yScale)

    if (index > 0) {
      val x1 = getX(index - 1, xAxisDistanceBetweenValues)
      val y1 = getY(values[index - 1], viewportHeight, minimumValue, yScale)
      val x2 = px
      val y2 = py
      val m = calculateSlope(x1, y1, x2, y2)

      when (index) {
        values.lastIndex -> {
          val c = y2 - m * x2
          val newY2 = m * x2 + c
          outPoints.add(Point(x1.toInt(), y1))
          outPoints.add(Point(viewportWidth, newY2.toInt()))
        }

        1 -> {
          val c = y1 - m * x1
          val newY1 = m * x1 + c
          outPoints.add(Point(X_ORIGIN, newY1.toInt()))
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
  xAxisDistanceBetweenValues: Double
): Double =
  index * xAxisDistanceBetweenValues

private fun getY(
  insertions: Int,
  viewportHeight: Int,
  lowestValue: Int,
  yScale: Int
): Int {
  val totalVerticalPadding = (viewportHeight * TOTAL_VERTICAL_PADDING_FRACTION).toInt()
  val actualGraphHeight = viewportHeight - totalVerticalPadding
  val scaledY = (insertions - lowestValue).toDouble() / yScale
  return ((actualGraphHeight + totalVerticalPadding / 2) - (scaledY * (actualGraphHeight)).toInt())
    .coerceAtMost(viewportHeight)
}
