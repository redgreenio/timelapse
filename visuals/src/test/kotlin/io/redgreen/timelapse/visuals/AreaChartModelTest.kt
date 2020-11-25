package io.redgreen.timelapse.visuals

import org.approvaltests.Approvals
import org.junit.jupiter.api.Test

class AreaChartModelTest {
  companion object {
    private const val HUNDRED = 100
  }

  @Test
  fun `increasing insertions`() {
    // given
    val insertionsOnlyCommits = listOf(1, 2, 3, 4, 5).map(::Commit)
    val outInsertionPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(insertionsOnlyCommits, HUNDRED, HUNDRED, outInsertionPoints)

    // then
    Approvals.verify(outInsertionPoints.humanize())
  }

  @Test
  fun `increasing insertions in a 0x0 area with 0 point radius`() {
    // given
    val side = 0
    val insertionsOnlyCommits = listOf(1, 2, 3, 4, 5).map(::Commit)
    val outInsertionPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(insertionsOnlyCommits, side, side, outInsertionPoints)

    // then
    Approvals.verify(outInsertionPoints.humanize())
  }

  @Test
  fun `varying insertions`() {
    // given
    val insertionsOnlyCommits = listOf(8, 7, 5, 4, 6).map(::Commit)
    val outInsertionPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(insertionsOnlyCommits, HUNDRED, HUNDRED, outInsertionPoints)

    // then
    Approvals.verify(outInsertionPoints.humanize())
  }

  @Test
  fun `negative insertion numbers`() {
    // given
    val insertionsOnlyCommits = listOf(-3 ,-2, -1, 0, 1, 2, 3).map(::Commit)
    val outInsertionPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(insertionsOnlyCommits, HUNDRED, HUNDRED, outInsertionPoints)

    // then
    Approvals.verify(outInsertionPoints.humanize())
  }

  @Test
  fun `insertions with no vertical padding`() {
    // given
    val insertionsOnlyCommits = listOf(0, 1, 2, 3, 4, 5).map(::Commit)
    val outInsertionPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(insertionsOnlyCommits, HUNDRED, HUNDRED, outInsertionPoints, mutableListOf(), 0.0)

    // then
    Approvals.verify(outInsertionPoints.humanize())
  }

  @Test
  fun `deletions with no vertical padding`() {
    // given
    val deletionsOnlyCommits = listOf(0, 1, 2, 3, 4, 5).map { Commit(0, it) }
    val outDeletionPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(deletionsOnlyCommits, HUNDRED, HUNDRED, mutableListOf(), outDeletionPoints, 0.0)

    // then
    Approvals.verify(outDeletionPoints.humanize())
  }

  @Test
  fun `insertions and deletions with no vertical padding`() {
    // given
    val commits = listOf(
      Commit(0, 0), // 0
      Commit(0, 1), // 1
      Commit(1, 1), // 2
      Commit(2, 1), // 3
      Commit(2, 2), // 4
      Commit(2, 3), // 5
    )
    val outInsertionPoints = mutableListOf<Point>()
    val outDeletionPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(commits, HUNDRED, HUNDRED, outInsertionPoints, outDeletionPoints, 0.0)

    // then
    Approvals.verify(
      """
        |Deletions
        |=========
        |${outDeletionPoints.humanize()}
        |
        |Insertions
        |==========
        |${outInsertionPoints.humanize()}
      """.trimMargin("|")
    )
  }

  private fun MutableList<Point>.humanize() =
    this.joinToString("\n") { "x=${it.x}, y=${it.y}" }
}
