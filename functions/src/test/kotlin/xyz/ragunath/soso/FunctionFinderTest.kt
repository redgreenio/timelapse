package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FunctionFinderTest {
  @Test
  fun `it returns an empty list when there are no possible functions`() {
    val thereIsNothingInHere = "  "

    assertThat(findPossibleFunctions(thereIsNothingInHere))
      .isEmpty()
  }

  @Test
  fun `it can return the line number and name when there is one possible function`() {
    val oneFunction = """
      fun main() { println("Hello World!") }
    """.trimIndent()

    assertThat(findPossibleFunctions(oneFunction))
      .containsExactly(PossibleFunction(1, "main"))
      .inOrder()
  }

  @Test
  fun `it can return the line numbers and names for two possible functions`() {
    val twoTopLevelFunctions = """
      fun main() {
      }

      fun printNothing() {
      }
    """.trimIndent()

    assertThat(findPossibleFunctions(twoTopLevelFunctions))
      .containsExactly(
        PossibleFunction(1, "main"),
        PossibleFunction(4, "printNothing")
      )
      .inOrder()
  }

  @Test
  fun `it can return the line number and name of one possible function declared inside a class`() {
    val classWithOneFunction = """
      class MrNobody {
        fun main() {
          // Do nothing
        }
      }
    """.trimIndent()

    assertThat(findPossibleFunctions(classWithOneFunction))
      .containsExactly(PossibleFunction(2, "main"))
      .inOrder()
  }

  @Test
  fun `it can return line numbers and names of multiple functions declared inside a class`() {
    val classWithThreeFunctions = """
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
    """.trimIndent()

    assertThat(findPossibleFunctions(classWithThreeFunctions))
      .containsExactly(
        PossibleFunction(4, "add"),
        PossibleFunction(8, "subtract"),
        PossibleFunction(12, "multiply")
      )
      .inOrder()
  }
}
