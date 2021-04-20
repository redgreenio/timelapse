package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import xyz.ragunath.soso.Result.WellFormedFunction
import xyz.ragunath.soso.kotlin.kotlinScan

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

    assertThat(getFunctionResults(::kotlinScan, functionWithTopLevelFunctions))
      .hasSize(3)

    assertThat(getFunctionResults(::kotlinScan, functionWithTopLevelFunctions))
      .containsExactly(
        WellFormedFunction.with(1, 3, 1),
        WellFormedFunction.with(5, 7, 1),
        WellFormedFunction.with(9, 11, 1)
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

    assertThat(getFunctionResults(::kotlinScan, fileWithClassFunctionsAndOneTopLevelFunction))
      .hasSize(4)

    assertThat(getFunctionResults(::kotlinScan, fileWithClassFunctionsAndOneTopLevelFunction))
      .containsExactly(
        WellFormedFunction.with(4, 6, 1),
        WellFormedFunction.with(8, 10, 1),
        WellFormedFunction.with(12, 14, 1),
        WellFormedFunction.with(17, 19, 1)
      )
      .inOrder()
  }

  @Test
  fun `it returns an empty list when there are no functions`() {
    val nothing = """
      Nothing to see here...
    """.trimIndent()

    assertThat(getFunctionResults(::kotlinScan, nothing))
      .isEmpty()
  }
}
