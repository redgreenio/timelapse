package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FunctionFinderTest {
  @Test
  fun `when there are no possible functions, then return an empty list`() {
    val thereIsNothingInHere = "  "

    assertThat(findPossibleFunctions(thereIsNothingInHere))
      .isEmpty()
  }

  @Test
  fun `when there is one possible function, then return its line number`() {
    val oneFunction = """
      fun main() { println("Hello World!") }
    """.trimIndent()

    assertThat(findPossibleFunctions(oneFunction))
      .containsExactly(PossibleFunction(1))
      .inOrder()
  }

  @Test
  fun `when there are more than one top-level functions, then return their line numbers`() {
    val twoTopLevelFunctions = """
      fun main() {
      }

      fun printNothing() {
      }
    """.trimIndent()

    assertThat(findPossibleFunctions(twoTopLevelFunctions))
      .containsExactly(PossibleFunction(1), PossibleFunction(4))
      .inOrder()
  }

  @Test
  fun `when there is one function nested inside a class, then return it's line number`() {
    val classWithOneFunction = """
      class MrNobody {
        fun main() {
          // Do nothing
        }
      }
    """.trimIndent()

    assertThat(findPossibleFunctions(classWithOneFunction))
      .containsExactly(PossibleFunction(2))
      .inOrder()
  }

  @Test
  fun `when there are 3 functions nested inside a class, then return their line numbers`() {
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
        PossibleFunction(4),
        PossibleFunction(8),
        PossibleFunction(12)
      )
      .inOrder()
  }
}
