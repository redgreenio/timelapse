package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.ParseResult
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges
import org.approvaltests.Approvals
import org.approvaltests.reporters.QuietReporter
import org.approvaltests.reporters.UseReporter
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

  @Test
  @UseReporter(QuietReporter::class)
  fun `it should handle deleted function`() {
    // given
    val kotlinSource = """
      fun b() {
      }
    """.trimIndent()

    val snippetA = """
      fun a() {
      }
    """.trimIndent()

    val comparisonResults = listOf(
      Deleted(ParseResult.wellFormedFunction("a", 1, 2, 1), snippetA)
    )

    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `it should handle modified and added functions`() {
    // given
    val swiftSource = """
      class SomeClass {
        func a() {
          // Hello, world!
        }
        
        fun b() {
          println("I am new!")
        }
      }
    """.trimIndent()

    val comparisonResults = listOf(
      Modified(ParseResult.wellFormedFunction("a", 2, 4, 1)),
      Added(ParseResult.wellFormedFunction("b", 6, 8, 1)),
    )

    val hasChanges = HasChanges(swiftSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  @UseReporter(QuietReporter::class)
  fun `it should handle added, modified and deleted functions`() {
    // given
    val kotlinSource = """
      function b() {
      }
      
      
      function d() {
        println("Knock, knock!")
      }
      
    """.trimIndent()

    val functionA = """
      fun a() {
        // Nothing here...
      }
    """.trimIndent()
    val functionC = """
      fun c(a: Int, b: Int): Int {
        return a + b
      }
    """.trimIndent()
    val functionE = """
      fun e(a: Double, b: Double): Double {
        return a * b
      }
    """.trimIndent()

    val comparisonResults = listOf(
      Deleted(ParseResult.wellFormedFunction("a", 1, 3, 1), functionA),
      Modified(ParseResult.wellFormedFunction("b", 1, 2, 1)),
      Deleted(ParseResult.wellFormedFunction("c", 4, 6, 1), functionC),
      Added(ParseResult.wellFormedFunction("d", 5, 7, 1)),
      Deleted(ParseResult.wellFormedFunction("e", 8, 10, 1), functionE)
    )

    // when
    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // then
    Approvals.verifyHtml(hasChanges.toHtml())
  }
}
