package io.redgreen.timelapse.visuals

import org.approvaltests.Approvals
import org.junit.jupiter.api.Test

class AreaChartModelTest {
  @Test
  fun `increasing insertions`() {
    // given
    val side = 100
    val pointRadius = 1
    val commits = listOf(1, 2, 3, 4, 5).map(::Commit)
    val outPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(commits, side, side, pointRadius, outPoints)

    // then
    Approvals.verify(outPoints.humanize())
  }

  @Test
  fun `increasing insertions in a 0x0 area with 0 point radius`() {
    // given
    val side = 0
    val pointRadius = 0
    val commits = listOf(1, 2, 3, 4, 5).map(::Commit)
    val outPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(commits, side, side, pointRadius, outPoints)

    // then
    Approvals.verify(outPoints.humanize())
  }

  @Test
  fun `varying insertions`() {
    // given
    val side = 100
    val pointRadius = 5
    val commits = listOf(8, 7, 5, 4, 6).map(::Commit)
    val outPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(commits, side, side, pointRadius, outPoints)

    // then
    Approvals.verify(outPoints.humanize())
  }

  @Test
  fun `negative numbers`() {
    // given
    val side = 100
    val pointRadius = 1
    val commits = listOf(-3 ,-2, -1, 0, 1, 2, 3).map(::Commit)
    val outPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(commits, side, side, pointRadius, outPoints)

    // then
    Approvals.verify(outPoints.humanize())
  }

  private fun MutableList<Point>.humanize() =
    this.joinToString("\n") { "x=${it.x}, y=${it.y}" }
}
