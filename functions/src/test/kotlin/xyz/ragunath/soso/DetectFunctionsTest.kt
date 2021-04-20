package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import xyz.ragunath.soso.languages.kotlin.kotlinScan

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

    assertThat(getParseResults(::kotlinScan, functionWithTopLevelFunctions))
      .hasSize(3)

    assertThat(getParseResults(::kotlinScan, functionWithTopLevelFunctions))
      .containsExactly(
        ParseResult.wellFormedFunction(1, 3, 1),
        ParseResult.wellFormedFunction(5, 7, 1),
        ParseResult.wellFormedFunction(9, 11, 1)
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

    assertThat(getParseResults(::kotlinScan, fileWithClassFunctionsAndOneTopLevelFunction))
      .hasSize(4)

    assertThat(getParseResults(::kotlinScan, fileWithClassFunctionsAndOneTopLevelFunction))
      .containsExactly(
        ParseResult.wellFormedFunction(4, 6, 1),
        ParseResult.wellFormedFunction(8, 10, 1),
        ParseResult.wellFormedFunction(12, 14, 1),
        ParseResult.wellFormedFunction(17, 19, 1)
      )
      .inOrder()
  }

  @Test
  fun `it returns an empty list when there are no functions`() {
    val nothing = """
      Nothing to see here...
    """.trimIndent()

    assertThat(getParseResults(::kotlinScan, nothing))
      .isEmpty()
  }
}
