package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.ParseResult
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges
import org.approvaltests.Approvals
import org.junit.jupiter.api.Test

class ExtendedDiffHtmlTest {
  @Test
  fun `it should handle added function`() {
    // given
    val kotlinSource = """
      fun a() {}
      fun b() {}
    """.trimIndent()

    val comparisonResults = listOf(
      ComparisonResult.Added(ParseResult.wellFormedFunction("b", 2, 2, 1))
    )

    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }
}
