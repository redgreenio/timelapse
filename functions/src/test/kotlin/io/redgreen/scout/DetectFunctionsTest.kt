package io.redgreen.scout

import com.google.common.truth.Truth.assertThat
import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import org.junit.jupiter.api.Test
import java.util.Optional

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
        ParseResult.wellFormedFunction(1, 3, 1, Optional.of(Name("add"))),
        ParseResult.wellFormedFunction(5, 7, 1, Optional.of(Name("subtract"))),
        ParseResult.wellFormedFunction(9, 11, 1, Optional.of(Name("multiply")))
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
        ParseResult.wellFormedFunction(4, 6, 1, Optional.of(Name("add"))),
        ParseResult.wellFormedFunction(8, 10, 1, Optional.of(Name("subtract"))),
        ParseResult.wellFormedFunction(12, 14, 1, Optional.of(Name("multiply"))),
        ParseResult.wellFormedFunction(17, 19, 1, Optional.of(Name("justOneTopLevelFunctionForKicks")))
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
