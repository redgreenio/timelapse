package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.ParseResult
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
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
      Added(ParseResult.wellFormedFunction("b", 2, 2, 1))
    )

    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `it should handle modified function`() {
    // given
    val swiftSource = """
      class SomeClass {
        func a() {
          // Hello, world!
        }
      }
    """.trimIndent()

    val comparisonResults = listOf(
      Modified(ParseResult.wellFormedFunction("a", 2, 4, 1))
    )

    val hasChanges = HasChanges(swiftSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }
}
