package io.redgreen.timelapse.extendeddiff

import com.google.common.truth.Truth.assertThat
import io.redgreen.scout.ParseResult
import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import org.junit.jupiter.api.Test

class CompareTest {
  @Test
  fun `it should detect an added function`() {
    // given
    val a = """
      fun a() {
      }
    """.trimIndent()

    val aDash = """
      fun a() {
      }

      fun b() {
      }
    """.trimIndent()

    // when
    val affectedFunctions = compare(a, aDash, KotlinFunctionScanner)

    // then
    assertThat(affectedFunctions)
      .containsExactly(
        ParseResult.wellFormedFunction("b", 4, 5, 1)
      )
  }

  @Test
  fun `it should detect several added functions`() {
    // given
    val a = """
      fun a() {
      }
    """.trimIndent()

    val aDash = """
      fun a() {
      }

      fun b() {
      }

      fun c() {
      }
    """.trimIndent()

    // when
    val affectedFunctions = compare(a, aDash, KotlinFunctionScanner)

    // then
    assertThat(affectedFunctions)
      .containsExactly(
        ParseResult.wellFormedFunction("b", 4, 5, 1),
        ParseResult.wellFormedFunction("c", 7, 8, 1)
      )
  }
}
