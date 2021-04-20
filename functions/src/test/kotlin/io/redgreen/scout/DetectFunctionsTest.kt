package io.redgreen.scout

import com.google.common.truth.Truth.assertThat
import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import org.junit.jupiter.api.Test

class DetectFunctionsTest {
  @Test
  fun `it can detect top-level functions from a file`() {
    val functionWithTopLevelFunctions = """
      fun add(a: Int, b: Int) {
        return a + b
      }

      fun subtract(a: Int, b: Int) {
        return a - b
      }

      fun multiply(a: Int, b: Int) {
        return a * b
      }
    """.trimIndent()

    assertThat(getParseResults(KotlinFunctionScanner::scan, functionWithTopLevelFunctions))
      .hasSize(3)

    assertThat(getParseResults(KotlinFunctionScanner::scan, functionWithTopLevelFunctions))
      .containsExactly(
        ParseResult.wellFormedFunction("add", 1, 3, 1),
        ParseResult.wellFormedFunction("subtract", 5, 7, 1),
        ParseResult.wellFormedFunction("multiply", 9, 11, 1)
      )
      .inOrder()
  }

  @Test
  fun `it can detect functions nested in a class`() {
    val fileWithClassFunctionsAndOneTopLevelFunction = """
      package xyz.ragunath.naive.calculator

      class Calculator {
        fun add(a: Int, b: Int) {
          return a + b
        }

        fun subtract(a: Int, b: Int) {
          return a - b
        }

        fun multiply(a: Int, b: Int) {
          return a * b
        }
      }

      fun justOneTopLevelFunctionForKicks() {
        println("Just for kicks!")
      }
    """.trimIndent()

    assertThat(getParseResults(KotlinFunctionScanner::scan, fileWithClassFunctionsAndOneTopLevelFunction))
      .hasSize(4)

    assertThat(getParseResults(KotlinFunctionScanner::scan, fileWithClassFunctionsAndOneTopLevelFunction))
      .containsExactly(
        ParseResult.wellFormedFunction("add", 4, 6, 1),
        ParseResult.wellFormedFunction("subtract", 8, 10, 1),
        ParseResult.wellFormedFunction("multiply", 12, 14, 1),
        ParseResult.wellFormedFunction("justOneTopLevelFunctionForKicks", 17, 19, 1)
      )
      .inOrder()
  }

  @Test
  fun `it returns an empty list when there are no functions`() {
    val nothing = """
      Nothing to see here...
    """.trimIndent()

    assertThat(getParseResults(KotlinFunctionScanner::scan, nothing))
      .isEmpty()
  }
}
