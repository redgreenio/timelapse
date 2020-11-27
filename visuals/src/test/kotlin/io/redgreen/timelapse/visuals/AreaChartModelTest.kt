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
    val insertionsOnlyCommits = listOf(1, 2, 3, 4, 5).map { Commit(it, 0) }
    val outInsertionPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(insertionsOnlyCommits, HUNDRED, HUNDRED, outInsertionPoints, mutableListOf())

    // then
    Approvals.verify(outInsertionPoints.humanize())
  }

  @Test
  fun `increasing insertions in a 0x0 area with 0 point radius`() {
    // given
    val side = 0
    val insertionsOnlyCommits = listOf(1, 2, 3, 4, 5).map { Commit(it, 0) }
    val outInsertionPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(insertionsOnlyCommits, side, side, outInsertionPoints, mutableListOf())

    // then
    Approvals.verify(outInsertionPoints.humanize())
  }

  @Test
  fun `varying insertions`() {
    // given
    val insertionsOnlyCommits = listOf(8, 7, 5, 4, 6).map { Commit(it, 0) }
    val outInsertionPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(insertionsOnlyCommits, HUNDRED, HUNDRED, outInsertionPoints, mutableListOf())

    // then
    Approvals.verify(outInsertionPoints.humanize())
  }

  @Test
  fun `insertions with no vertical padding`() {
    // given
    val insertionsOnlyCommits = listOf(0, 1, 2, 3, 4, 5).map { Commit(it, 0) }
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
    Approvals.verify(humanizeDeletionsInsertions(outDeletionPoints, outInsertionPoints))
  }

  @Test
  fun `insertions and deletions with alternating zeros and no vertical padding`() {
    // given
    val commits = listOf(
      Commit(5, 0),
      Commit(1, 0),
      Commit(1, 4),
      Commit(2, 0),
      Commit(0, 1),
      Commit(1, 0),
      Commit(1, 0),
    )
    val outInsertionPoints = mutableListOf<Point>()
    val outDeletionPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(commits, HUNDRED, HUNDRED, outInsertionPoints, outDeletionPoints, 0.0)

    // then
    Approvals.verify(humanizeDeletionsInsertions(outDeletionPoints, outInsertionPoints))
  }

  @Test
  fun `minimum value should not be scaled down to zero`() {
    // given
    val commits = listOf(
      Commit(1, 1),
      Commit(1, 1),
      Commit(1, 0),
      Commit(1, 0),
      Commit(1, 1),
      Commit(1, 1),
      Commit(1, 0),
      Commit(1, 2),
      Commit(1, 1),
      Commit(3, 1),
    )
    val outInsertionPoints = mutableListOf<Point>()
    val outDeletionPoints = mutableListOf<Point>()

    // when
    computePolygonPoints(commits, HUNDRED, HUNDRED, outInsertionPoints, outDeletionPoints, 0.0)

    // then
    Approvals.verify(humanizeDeletionsInsertions(outDeletionPoints, outInsertionPoints))
  }

  private fun humanizeDeletionsInsertions(
    outDeletionPoints: MutableList<Point>,
    outInsertionPoints: MutableList<Point>
  ): String {
    return """
      |Deletions
      |=========
      |${outDeletionPoints.humanize()}
      |
      |Insertions
      |==========
      |${outInsertionPoints.humanize()}
    """.trimMargin("|")
  }

  private fun MutableList<Point>.humanize() =
    this.joinToString("\n") { "x=${it.x}, y=${it.y}" }
}
