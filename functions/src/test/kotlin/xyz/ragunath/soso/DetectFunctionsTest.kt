package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
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

    assertThat(detectFunctions(functionWithTopLevelFunctions))
      .hasSize(3)

    assertThat(detectFunctions(functionWithTopLevelFunctions))
      .containsExactly(
        Result.with(1, 3, 1),
        Result.with(5, 7, 1),
        Result.with(9, 11, 1)
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

    assertThat(detectFunctions(fileWithClassFunctionsAndOneTopLevelFunction))
      .hasSize(4)

    assertThat(detectFunctions(fileWithClassFunctionsAndOneTopLevelFunction))
      .containsExactly(
        Result.with(4, 6, 1),
        Result.with(8, 10, 1),
        Result.with(12, 14, 1),
        Result.with(17, 19, 1)
      )
      .inOrder()
  }
}
