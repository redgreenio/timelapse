package io.redgreen.timelapse.extendeddiff

import com.google.common.truth.Truth.assertThat
import io.redgreen.scout.ParseResult
import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
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
    val affectedFunctions = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(affectedFunctions)
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
    val affectedFunctions = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(affectedFunctions)
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
    val affectedFunctions = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(affectedFunctions)
      .containsExactly(
        Deleted(ParseResult.wellFormedFunction("b", 4, 5, 1))
      )
  }
}
