package io.redgreen.timelapse.extendeddiff

import com.google.common.truth.Truth.assertThat
import io.redgreen.scout.ParseResult
import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
import org.junit.jupiter.api.Test

class CompareTest {
  @Test
  fun `it should detect an added function`() {
    // given
    val before = """
      fun a() {
      }
    """.trimIndent()

    val after = """
      fun a() {
      }

      fun b() {
      }
    """.trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(comparisonResults)
      .containsExactly(
        Added(ParseResult.wellFormedFunction("b", 4, 5, 1))
      )
  }

  @Test
  fun `it should detect several added functions`() {
    // given
    val before = """
      fun a() {
      }
    """.trimIndent()

    val after = """
      fun a() {
      }

      fun b() {
      }

      fun c() {
      }
    """.trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(comparisonResults)
      .containsExactly(
        Added(ParseResult.wellFormedFunction("b", 4, 5, 1)),
        Added(ParseResult.wellFormedFunction("c", 7, 8, 1))
      )
      .inOrder()
  }

  @Test
  fun `it should detect a deleted function`() {
    // given
    val before = """
      fun a() {
      }

      fun b() {
      }
    """.trimIndent()

    val after = """
      fun a() {
      }
    """.trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    val deletedFunctionSnippet = """
      fun b() {
      }
    """.trimIndent()
    assertThat(comparisonResults)
      .containsExactly(
        Deleted(ParseResult.wellFormedFunction("b", 4, 5, 1), deletedFunctionSnippet)
      )
  }

  @Test
  fun `it should detect several deleted functions`() {
    // given
    val before = """
      fun a() {
      }

      fun b() {
      }
    """.trimIndent()

    val after = "".trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    val snippetA = """
      fun a() {
      }
    """.trimIndent()

    val snippetB = """
      fun b() {
      }
    """.trimIndent()
    assertThat(comparisonResults)
      .containsExactly(
        Deleted(ParseResult.wellFormedFunction("a", 1, 2, 1), snippetA),
        Deleted(ParseResult.wellFormedFunction("b", 4, 5, 1), snippetB)
      )
  }

  @Test
  fun `it should detect a modified function`() {
    // given
    val before = """
      fun a() {
      }
    """.trimIndent()

    val after = """
      fun a() {
        println("Hello, world!")
      }
    """.trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(comparisonResults)
      .containsExactly(
        Modified(ParseResult.wellFormedFunction("a", 1, 3, 1), after)
      )
  }

  @Test
  fun `it should return an empty result if the functions moved around without any changes`() {
    // given
    val before = """
      fun a() {
      }

      fun b() {
      }
    """.trimIndent()

    val after = """
      fun b() {
      }

      fun a() {
      }
    """.trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(comparisonResults)
      .isEmpty()
  }

  @Test
  fun `it should detect added, modified, and deleted functions`() {
    // given
    val before = """
      fun a() {
      }

      fun b() {
      }

      fun c() {
      }
    """.trimIndent()

    val after = """
      fun a() {
      }

      fun x() {
      }

      fun c() {
        println("Hello, world!")
      }
    """.trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    val snippetB = """
      fun b() {
      }
    """.trimIndent()
    val snippetC = """
      fun c() {
        println("Hello, world!")
      }
    """.trimIndent()

    assertThat(comparisonResults)
      .containsExactly(
        Deleted(ParseResult.wellFormedFunction("b", 4, 5, 1), snippetB),
        Added(ParseResult.wellFormedFunction("x", 4, 5, 1)),
        Modified(ParseResult.wellFormedFunction("c", 7, 9, 1), snippetC)
      )
  }

  @Test
  fun `it should only detect functions with changes to their function body`() {
    // given
    val before = """
      fun a() {
        // Hello, world!
      }

      fun b() {
      }

      fun c() {
      }
    """.trimIndent()

    val after = """
      fun a() {
      }

      fun b() {
      }

      fun c() {
      }
    """.trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    val functionA = """
      fun a() {
      }
    """.trimIndent()

    assertThat(comparisonResults)
      .containsExactly(
        Modified(ParseResult.wellFormedFunction("a", 1, 2, 1), functionA)
      )
  }
}
