package io.redgreen.timelapse.extendeddiff

import com.google.common.truth.Truth.assertThat
import io.redgreen.scout.ParseResult
import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Renamed
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Unmodified
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
        Added(ParseResult.wellFormedFunction("b", 4, 5)),
        Unmodified(ParseResult.wellFormedFunction("a", 1, 2)),
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
        Added(ParseResult.wellFormedFunction("b", 4, 5)),
        Added(ParseResult.wellFormedFunction("c", 7, 8)),
        Unmodified(ParseResult.wellFormedFunction("a", 1, 2)),
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
        Deleted(ParseResult.wellFormedFunction("b", 4, 5), deletedFunctionSnippet),
        Unmodified(ParseResult.wellFormedFunction("a", 1, 2)),
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
        Deleted(ParseResult.wellFormedFunction("a", 1, 2), snippetA),
        Deleted(ParseResult.wellFormedFunction("b", 4, 5), snippetB)
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
        Modified(ParseResult.wellFormedFunction("a", 1, 3), after)
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
      .containsExactly(
        Unmodified(ParseResult.wellFormedFunction("b", 1, 2)),
        Unmodified(ParseResult.wellFormedFunction("a", 4, 5))
      )
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
        Deleted(ParseResult.wellFormedFunction("b", 4, 5), snippetB),
        Added(ParseResult.wellFormedFunction("x", 4, 5)),
        Modified(ParseResult.wellFormedFunction("c", 7, 9), snippetC),
        Unmodified(ParseResult.wellFormedFunction("a", 1, 2))
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
        Modified(ParseResult.wellFormedFunction("a", 1, 2), functionA),
        Unmodified(ParseResult.wellFormedFunction("b", 4, 5)),
        Unmodified(ParseResult.wellFormedFunction("c", 7, 8)),
      )
  }

  @Test
  fun `it should detect modified functions`() {
    // given
    val before = """
      fun hello() {
        // Did I change?
      }
    """.trimIndent()

    val after = """
      fun hello() {
        // Yes, I did!
      }
    """.trimIndent()

    // when
    val comparisonResult = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(comparisonResult)
      .containsExactly(
        Modified(ParseResult.wellFormedFunction("hello", 1, 3), after)
      )
  }

  @Test
  fun `it should detect unmodified functions`() {
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
      fun b() {
      }

      fun c() {
      }
    """.trimIndent()

    // when
    val comparisonResult = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(comparisonResult.filterIsInstance<Unmodified>())
      .hasSize(2)
  }

  @Test
  fun `it should detect renamed functions`() {
    // given
    val before = """
      fun greet() {
        println("Hello, world!")
      }
    """.trimIndent()

    val after = """
      fun sayHello() {
        println("Hello, world!")
      }
    """.trimIndent()

    // when
    val comparisonResult = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(comparisonResult)
      .containsExactly(
        Renamed(ParseResult.wellFormedFunction("sayHello", 1, 3), "greet")
      )
  }

  @Test
  fun `it should detect renamed functions even if the parameters are put in different lines`() {
    // given
    val before = """
      fun plus(x: Int, y: Int): Int {
        return x + y
      }
    """.trimIndent()

    val after = """
      fun add(
        x: Int,
        y: Int
      ): Int {
        return x + y
      }
    """.trimIndent()

    // when
    val comparisonResult = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(comparisonResult)
      .containsExactly(
        Renamed(ParseResult.wellFormedFunction("add", 1, 6), "plus")
      )
  }
}
