package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FunctionAnalyzerTest {
  @Test
  fun `it returns and empty result for blank snippets`() {
    val noBrackets = "    "

    assertThat(analyze(noBrackets))
      .isEmpty()
  }

  @Test
  fun `it returns an empty result for non-code snippets`() {
    val justComments = """
      // This is just some comment,
      // followed by another line of comment,
      // and yet another line of comment!
    """.trimIndent()

    assertThat(analyze(justComments))
      .isEmpty()
  }

  @Test
  fun `it can analyze matching brackets in the same line`() {
    val onePairOfBracketsSingleLine = "{}"
    val expectedResult = Result.with(1, 1, 1)

    val actualResults = analyze(onePairOfBracketsSingleLine)
    assertThat(actualResults)
      .containsExactly(expectedResult)
      .inOrder()

    assertThat(actualResults.first().length)
      .isEqualTo(1)
  }

  @Test
  fun `it can analyze two pairs of matching brackets in the same line`() {
    val twoPairsOfBracketsSingleLine = "{{}}"
    val expectedResult = Result.with(1, 1, 2)

    assertThat(analyze(twoPairsOfBracketsSingleLine))
      .containsExactly(expectedResult)
      .inOrder()
  }

  @Test
  fun `it can analyze matching brackets on two lines`() {
    val onePairOfBracketDifferentLines = """
        {
        }
      """.trimIndent()
    val expectedResult = Result.with(1, 2, 1)

    assertThat(analyze(onePairOfBracketDifferentLines))
      .containsExactly(expectedResult)
      .inOrder()
  }

  @Test
  fun `it can analyze a Kotlin top-level function with a depth of 1 and length of 2`() {
    val mainFunction = """
      fun main() {
      }
    """.trimIndent()
    val expectedResult = Result.with(1, 2, 1)

    assertThat(analyze(mainFunction))
      .containsExactly(expectedResult)
      .inOrder()
  }

  @Test
  fun `it can analyze a Kotlin top-level function with a depth of 2 and a length of 5`() {
    val functionWithConditional = """
      fun main() {
        if (true) {
          // Doesn't matter
        }
      }
    """.trimIndent()
    val expectedResult = Result.with(1, 5, 2)

    assertThat(analyze(functionWithConditional))
      .containsExactly(expectedResult)
      .inOrder()
  }

  @Test
  fun `it can analyze a Kotlin function with a depth of 2 and length of 9`() {
    val functionWithIfElseLadder = """
      fun main() {
        if (true) {
          // Doesn't matter
        } else if (!false) {
          // Neither here... ¯\_(ツ)_/¯
        } else {
          // Nor here...
        }
      }
    """.trimIndent()
    val expectedResult = Result.with(1, 9, 2)

    assertThat(analyze(functionWithIfElseLadder))
      .containsExactly(expectedResult)
      .inOrder()
  }

  @Test
  fun `it can analyze a Kotlin function with a depth of 5 and a length of 18`() {
    val functionWith4LevelsOfNesting = """
      fun notFun() {
        // Level 1
        if (true) {

          // Level 2
          if (!false) {

            // Level 3
            if (true) {

              // Level 4
              while (true) {
                println("Hello World!")
              }
            }
          }
        }
      }
    """.trimIndent()
    val expectedResult = Result.with(1, 18, 5)

    val actualResults = analyze(functionWith4LevelsOfNesting)
    assertThat(actualResults)
      .containsExactly(expectedResult)
      .inOrder()
    assertThat(actualResults.first().length)
      .isEqualTo(18)
  }

  @Test
  fun `it can ignore brackets in single-line comments`() {
    val functionWithCommentedMatchingBraces = """
      fun main() {
        // { /* nothing here */ }
        //{}
      }
    """.trimIndent()
    val expectedResult = Result.with(1, 4, 1)

    assertThat(analyze(functionWithCommentedMatchingBraces))
      .containsExactly(expectedResult)
      .inOrder()
  }

  @Test
  fun `it can ignore code commented out using a single-line comment`() {
    val functionWithCommentedCode = """
      fun main() {
        // if (true) {
        //   print("TRUE")
        // } else {
        //   print("NOT TRUE")
        // }
      }
    """.trimIndent()
    val expectedResult = Result.with(1, 7, 1)

    assertThat(analyze(functionWithCommentedCode))
      .containsExactly(expectedResult)
      .inOrder()
  }

  @Test
  fun `it can ignore brackets inside multiline comments`() {
    val functionWithCommentedMatchingBraces = """
      fun main() {
        /* { nothing here } */
        /*{ anything can go here }*/
      }
    """.trimIndent()
    val expectedResult = Result.with(1, 4, 1)

    assertThat(analyze(functionWithCommentedMatchingBraces))
      .containsExactly(expectedResult)
      .inOrder()
  }

  @Test
  fun `it can ignore brackets inside multiline comments that span across lines`() {
    val functionWithMultilineCommentedCode = """
      fun main() {
        /*
        if (true) {
          println("Good food, yum!")
        }
        */
      }
    """.trimIndent()
    val expectedResult = Result.with(1, 7, 1)

    assertThat(analyze(functionWithMultilineCommentedCode))
      .containsExactly(expectedResult)
      .inOrder()
  }

  @Test
  fun `it can ignore nested single-line comments`() {
    val functionWithNestedSingleLineComments = """
      fun main() {
        // {} //
      }
    """.trimIndent()
    val expectedResult = Result.with(1, 3, 1)

    assertThat(analyze(functionWithNestedSingleLineComments))
      .containsExactly(expectedResult)
      .inOrder()
  }

  @Test
  fun `it can ignore nested multiline comments`() {
    val functionWithNestedMultilineComments = """
      fun main() {
        /*
          /* {} */
          if (!false) {
            // Do nothing.
          } /* */
        */
        if (true) {
          println("Ahoy!")
        }
      }
    """.trimIndent()
    val expectedResult = Result.with(1, 11, 2)

    assertThat(analyze(functionWithNestedMultilineComments))
      .containsExactly(expectedResult)
      .inOrder()
  }

  @Test
  fun `it can find the function's start and end lines`() {
    val functionDeclarationWithPackageName = """
      package a.b.c

      fun main() {
        // Do nothing...
      }
    """.trimIndent()
    val expectedResult = Result.with(3, 5, 1)

    assertThat(analyze(functionDeclarationWithPackageName))
      .containsExactly(expectedResult)
      .inOrder()
  }

  @Test
  fun `it can find multiple top-level functions in a given file`() {
    val multipleTopLevelFunctions = """
      package a.b.c

      fun main() {
        // Do nothing...
      }

      fun add(a: Int, b: Int): Int {
        return a + b
      }

      fun subtract(a: Int, b: Int): Int {
        return if (a > b) {
          a - b
        } else {
          b - a
        }
      }
    """.trimIndent()
    val results = analyze(multipleTopLevelFunctions)

    assertThat(results)
      .containsExactly(
        Result.with(3, 5, 1),
        Result.with(7, 9, 1),
        Result.with(11, 17, 2)
      )
      .inOrder()
  }

  // TODO Derive length of the function from start row and end row
  // TODO Ignore new lines after the function
  // TODO What is a depth/nesting of a function?
  // TODO Find the system newline character in a given file
  // TODO Find character encoding in a given file
}
