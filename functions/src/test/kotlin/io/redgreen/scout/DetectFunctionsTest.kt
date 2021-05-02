package io.redgreen.scout

import com.google.common.truth.Truth.assertThat
import io.redgreen.scout.extensions.readResourceFile
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

    assertThat(getParseResults(functionWithTopLevelFunctions, KotlinFunctionScanner::scan))
      .hasSize(3)

    assertThat(getParseResults(functionWithTopLevelFunctions, KotlinFunctionScanner::scan))
      .containsExactly(
        ParseResult.wellFormedFunction("add", 1, 3),
        ParseResult.wellFormedFunction("subtract", 5, 7),
        ParseResult.wellFormedFunction("multiply", 9, 11)
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

    assertThat(getParseResults(fileWithClassFunctionsAndOneTopLevelFunction, KotlinFunctionScanner::scan))
      .hasSize(4)

    assertThat(getParseResults(fileWithClassFunctionsAndOneTopLevelFunction, KotlinFunctionScanner::scan))
      .containsExactly(
        ParseResult.wellFormedFunction("add", 4, 6),
        ParseResult.wellFormedFunction("subtract", 8, 10),
        ParseResult.wellFormedFunction("multiply", 12, 14),
        ParseResult.wellFormedFunction("justOneTopLevelFunctionForKicks", 17, 19)
      )
      .inOrder()
  }

  @Test
  fun `it returns an empty list when there are no functions`() {
    val nothing = """
      Nothing to see here...
    """.trimIndent()

    assertThat(getParseResults(nothing, KotlinFunctionScanner::scan))
      .isEmpty()
  }

  @Test
  fun `it should detect extension functions`() {
    val extensionFunction = """
      fun String.fullStop(): String {
        return "$this."
      }
    """.trimIndent()

    assertThat(getParseResults(extensionFunction, KotlinFunctionScanner::scan))
      .containsExactly(
        ParseResult.wellFormedFunction("String.fullStop", 1, 3)
      )
  }

  @Test
  fun `it should ignore functions declared inside multiline strings`() {
    // given
    val functionDeclaredInsideMultilineString = "fun HelloWorld.x(): String {\n" +
      "  return \"\"\"fun a() {}\"\"\"\n" +
      "}"

    // when & then
    assertThat(getParseResults(functionDeclaredInsideMultilineString, KotlinFunctionScanner::scan))
      .containsExactly(
        ParseResult.wellFormedFunction("HelloWorld.x", 1, 3)
      )
  }

  @Test
  fun `it should ignore functions declared inside multiline strings spanned across multiple lines`() {
    // given
    val multilineStringContainingFunctions = readResourceFile("/multiline_string_containing_functions")

    // when & then
    assertThat(getParseResults(multilineStringContainingFunctions, KotlinFunctionScanner::scan))
      .containsExactly(
        ParseResult.wellFormedFunction("HelloWorld.justAnotherFunction", 1, 10)
      )
  }

  @Test
  fun `it can detect function with css, html, and multiline string`() {
    // given
    val snippetWithCssAndMultilineStrings = readResourceFile("/function_returning_html")

    // when & then
    assertThat(getParseResults(snippetWithCssAndMultilineStrings, KotlinFunctionScanner::scan))
      .containsExactly(
        ParseResult.wellFormedFunction("html", 1, 26)
      )
  }

  @Test
  fun `it should detect functions amidst of multiline strings, complex curly braces, html, and css`() {
    // given
    val trickyKotlinFile = readResourceFile("/file_with_several_functions_and_tricky_curly_braces")

    // when & then
    assertThat(getParseResults(trickyKotlinFile, KotlinFunctionScanner::scan))
      .containsExactly(
        ParseResult.wellFormedFunction("ExtendedDiff.toHtml", 10, 130),
        ParseResult.wellFormedFunction("toRows", 132, 151),
        ParseResult.wellFormedFunction("classAttribute", 153, 159),
        ParseResult.wellFormedFunction("offsetWithPadding", 161, 173),
      )
  }
}
