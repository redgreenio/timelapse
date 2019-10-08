package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SoSoTest {
  @Test
  fun `empty result has 0 depth and 0 length`() {
    assertThat(Result.EMPTY.depth).isEqualTo(0)
    assertThat(Result.EMPTY.length).isEqualTo(0)
  }

  @Test
  fun `it returns just the line count for blank snippets`() {
    val noBrackets = "    "

    assertThat(SoSo.analyze(noBrackets))
      .isEqualTo(Result(0, 1))
  }

  @Test
  fun `it returns just the line count for non-code snippets`() {
    val justComments = """
      // This is just some comment,
      // followed by another line of comment,
      // and yet another line of comment!
    """.trimIndent()

    assertThat(SoSo.analyze(justComments))
      .isEqualTo(Result(0, 3))
  }

  @Test
  fun `it can analyze matching brackets in the same line`() {
    val onePairOfBracketsSingleLine = "{}"
    val expectedResult = Result(1, 1)

    assertThat(SoSo.analyze(onePairOfBracketsSingleLine))
      .isEqualTo(expectedResult)
  }

  @Test
  fun `it can analyze two pairs of matching brackets in the same line`() {
    val twoPairsOfBracketsSingleLine = "{{}}"
    val expectedResult = Result(2, 1)

    assertThat(SoSo.analyze(twoPairsOfBracketsSingleLine))
      .isEqualTo(expectedResult)
  }

  @Test
  fun `it can analyze matching brackets on two lines`() {
    val onePairOfBracketDifferentLines = """
        {
        }
      """.trimIndent()
    val expectedResult = Result(1, 2)

    assertThat(SoSo.analyze(onePairOfBracketDifferentLines))
      .isEqualTo(expectedResult)
  }

  @Test
  fun `it can analyze a Kotlin top-level function with a depth of 1 and length of 2`() {
    val mainFunction = """
      fun main() {
      }
    """.trimIndent()
    val expectedResult = Result(1, 2)

    assertThat(SoSo.analyze(mainFunction))
      .isEqualTo(expectedResult)
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
    val expectedResult = Result(2, 5)

    assertThat(SoSo.analyze(functionWithConditional))
      .isEqualTo(expectedResult)
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
    val expectedResult = Result(2, 9)

    assertThat(SoSo.analyze(functionWithIfElseLadder))
      .isEqualTo(expectedResult)
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
    val expectedResult = Result(5, 18)

    assertThat(SoSo.analyze(functionWith4LevelsOfNesting))
      .isEqualTo(expectedResult)
  }

  @Test
  fun `it can ignore brackets in single-line comments`() {
    val functionWithCommentedMatchingBraces = """
      fun main() {
        // { /* nothing here */ }
        //{}
      }
    """.trimIndent()
    val expectedResult = Result(1, 4)

    assertThat(SoSo.analyze(functionWithCommentedMatchingBraces))
      .isEqualTo(expectedResult)
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
    val expectedResult = Result(1, 7)

    assertThat(SoSo.analyze(functionWithCommentedCode))
      .isEqualTo(expectedResult)
  }

  @Test
  fun `it can ignore brackets inside multiline comments`() {
    val functionWithCommentedMatchingBraces = """
      fun main() {
        /* { nothing here } */
        /*{ anything can go here }*/
      }
    """.trimIndent()
    val expectedResult = Result(1, 4)

    assertThat(SoSo.analyze(functionWithCommentedMatchingBraces))
      .isEqualTo(expectedResult)
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
    val expectedResult = Result(1, 7)

    assertThat(SoSo.analyze(functionWithMultilineCommentedCode))
      .isEqualTo(expectedResult)
  }

  @Test
  fun `it can ignore nested single-line comments`() {
    val functionWithNestedSingleLineComments = """
      fun main() {
        // {} //
      }
    """.trimIndent()
    val expectedResult = Result(1, 3)

    assertThat(SoSo.analyze(functionWithNestedSingleLineComments))
      .isEqualTo(expectedResult)
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
    val expectedResult = Result(2, 11)

    assertThat(SoSo.analyze(functionWithNestedMultilineComments))
      .isEqualTo(expectedResult)
  }

  // TODO Ignore new lines after the function
  // TODO What is a depth/nesting of a function?
  // TODO Find the system newline character in a given file
  // TODO Find character encoding in a given file
}
