package io.redgreen.scout.languages.kotlin

import com.google.common.truth.Truth.assertThat
import io.redgreen.scout.PossibleFunction
import io.redgreen.scout.extensions.readResourceFile
import org.junit.jupiter.api.Test

class KotlinFunctionScannerTest {
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

  @Test
  fun `it should return an empty list for a file with comment containing the word function`() {
    // given
    val compilationUnitWithComment = """
      /* function */
    """.trimIndent()

    // when & then
    assertThat(KotlinFunctionScanner.scan(compilationUnitWithComment))
      .isEmpty()
  }

  @Test
  fun `it should return an empty list for a file with comment containing the word 'fun '`() {
    // given
    val compilationUnitWithComment = """
      /* it's going to be so much fun ! */
    """.trimIndent()

    // when & then
    assertThat(KotlinFunctionScanner.scan(compilationUnitWithComment))
      .isEmpty()
  }

  @Test
  fun `it should skip functions within multiline string literals`() {
    // given
    val functionDeclaredInsideMultilineString = "fun HelloWorld.x(): String {\n" +
      "  return \"\"\"fun a() {}\"\"\"\n" +
      "}"

    // when & then
    assertThat(KotlinFunctionScanner.scan(functionDeclaredInsideMultilineString))
      .containsExactly(PossibleFunction("HelloWorld.x", 1))
  }

  @Test
  fun `it should ignore braces within multiline strings`() {
    // given
    val snippetWithCssAndMultilineStrings = readResourceFile("/function_returning_html")

    // when & then
    assertThat(KotlinFunctionScanner.scan(snippetWithCssAndMultilineStrings))
      .containsExactly(PossibleFunction("html", 1))
  }

  @Test
  fun `it should detect functions amidst of multiline strings, complex curly braces, html, and css`() {
    // given
    val trickyKotlinFile = readResourceFile("/file_with_several_functions_and_tricky_curly_braces")

    // when & then
    assertThat(KotlinFunctionScanner.scan(trickyKotlinFile))
      .containsExactly(
        PossibleFunction("ExtendedDiff.toHtml", 10),
        PossibleFunction("toRows", 132),
        PossibleFunction("classAttribute", 153),
        PossibleFunction("offsetWithPadding", 161),
      )
  }
}
