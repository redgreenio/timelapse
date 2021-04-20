package io.redgreen.scout

import com.google.common.truth.Truth.assertThat
import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import org.junit.jupiter.api.Test

class FindPossibleFunctionsTest {
  @Test
  fun `it returns an empty list when there are no possible functions`() {
    val thereIsNothingInHere = "  "

    assertThat(KotlinFunctionScanner.scan(thereIsNothingInHere))
      .isEmpty()
  }

  @Test
  fun `it can return the line number and name when there is one possible function`() {
    val oneFunction = """
      fun main() { println("Hello World!") }
    """.trimIndent()

    assertThat(KotlinFunctionScanner.scan(oneFunction))
      .containsExactly(PossibleFunction("main", 1))
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

    assertThat(KotlinFunctionScanner.scan(twoTopLevelFunctions))
      .containsExactly(
        PossibleFunction("main", 1),
        PossibleFunction("printNothing", 4)
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

    assertThat(KotlinFunctionScanner.scan(classWithOneFunction))
      .containsExactly(PossibleFunction("main", 2))
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

    assertThat(KotlinFunctionScanner.scan(classWithThreeFunctions))
      .containsExactly(
        PossibleFunction("add", 4),
        PossibleFunction("subtract", 8),
        PossibleFunction("multiply", 12)
      )
      .inOrder()
  }
}
