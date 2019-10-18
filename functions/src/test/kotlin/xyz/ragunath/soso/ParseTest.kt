package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import xyz.ragunath.soso.ParseResult.MalformedFunction
import xyz.ragunath.soso.ParseResult.Nothing
import xyz.ragunath.soso.ParseResult.WellFormedFunction

class ParseTest {
  @Test
  fun `it returns and empty result for blank snippets`() {
    val noBrackets = "    "

    assertThat(parse(noBrackets))
      .isEqualTo(Nothing)
  }

  @Test
  fun `it returns an empty result for non-code snippets`() {
    val justComments = """
      // This is just some comment,
      // followed by another line of comment,
      // and yet another line of comment!
    """.trimIndent()

    assertThat(parse(justComments))
      .isEqualTo(Nothing)
  }

  @Test
  fun `it can analyze matching brackets in the same line`() {
    val onePairOfBracketsSingleLine = "{}"
    val expectedResult = WellFormedFunction.with(1, 1, 1)

    val actualResults = parse(onePairOfBracketsSingleLine)
    assertThat(actualResults)
      .isEqualTo(expectedResult)

    assertThat((actualResults as WellFormedFunction).length)
      .isEqualTo(1)
  }

  @Test
  fun `it can analyze two pairs of matching brackets in the same line`() {
    val twoPairsOfBracketsSingleLine = "{{}}"
    val expectedResult = WellFormedFunction.with(1, 1, 2)

    assertThat(parse(twoPairsOfBracketsSingleLine))
      .isEqualTo(expectedResult)
  }

  @Test
  fun `it can analyze matching brackets on two lines`() {
    val onePairOfBracketDifferentLines = """
        {
        }
      """.trimIndent()
    val expectedResult = WellFormedFunction.with(1, 2, 1)

    assertThat(parse(onePairOfBracketDifferentLines))
      .isEqualTo(expectedResult)
  }

  @Test
  fun `it can analyze a Kotlin top-level function with a depth of 1 and length of 2`() {
    val mainFunction = """
      fun main() {
      }
    """.trimIndent()
    val expectedResult = WellFormedFunction.with(1, 2, 1)

    assertThat(parse(mainFunction))
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
    val expectedResult = WellFormedFunction.with(1, 5, 2)

    assertThat(parse(functionWithConditional))
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
    val expectedResult = WellFormedFunction.with(1, 9, 2)

    assertThat(parse(functionWithIfElseLadder))
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
    val expectedResult = WellFormedFunction.with(1, 18, 5)

    val actualResults = parse(functionWith4LevelsOfNesting)
    assertThat(actualResults)
      .isEqualTo(expectedResult)
    assertThat((actualResults as WellFormedFunction).length)
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
    val expectedResult = WellFormedFunction.with(1, 4, 1)

    assertThat(parse(functionWithCommentedMatchingBraces))
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
    val expectedResult = WellFormedFunction.with(1, 7, 1)

    assertThat(parse(functionWithCommentedCode))
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
    val expectedResult = WellFormedFunction.with(1, 4, 1)

    assertThat(parse(functionWithCommentedMatchingBraces))
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
    val expectedResult = WellFormedFunction.with(1, 7, 1)

    assertThat(parse(functionWithMultilineCommentedCode))
      .isEqualTo(expectedResult)
  }

  @Test
  fun `it can ignore nested single-line comments`() {
    val functionWithNestedSingleLineComments = """
      fun main() {
        // {} //
      }
    """.trimIndent()
    val expectedResult = WellFormedFunction.with(1, 3, 1)

    assertThat(parse(functionWithNestedSingleLineComments))
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
    val expectedResult = WellFormedFunction.with(1, 11, 2)

    assertThat(parse(functionWithNestedMultilineComments))
      .isEqualTo(expectedResult)
  }

  @Test
  fun `it can find the function's start and end lines`() {
    val functionDeclarationWithPackageName = """
      package a.b.c

      fun main() {
        // Do nothing...
      }
    """.trimIndent()
    val expectedResult = WellFormedFunction.with(3, 5, 1)

    assertThat(parse(functionDeclarationWithPackageName))
      .isEqualTo(expectedResult)
  }

  @Test
  fun `it can return as soon as it finds a matching top-level bracket`() {
    val functionWithNonMatchingBracket = """
        fun multiply(a: Int, b: Int) {
          return a * b
        }
      }
    """.trimIndent()

    assertThat(parse(functionWithNonMatchingBracket))
      .isEqualTo(WellFormedFunction.with(1, 3, 1))
  }

  @Test
  fun `it can ignore contents of string literals in function signatures`() {
    val functionWithForwardSlashesInStringLiteral = """
      static func make(url: URL = URL(string: "https://httpbin.org/get")!, method: HTTPMethod = .get, headers: HTTPHeaders = .init()) -> URLRequest {
        var request = URLRequest(url: url)
        request.method = method
        request.headers = headers

        return request
      }
    """.trimIndent()

    assertThat(parse(functionWithForwardSlashesInStringLiteral))
      .isEqualTo(WellFormedFunction.with(1, 7, 1))
  }

  @Test
  fun `it can ignore contents of multiline string literals`() {
    val functionWithMultilineStringLiteral =
        "func multilineStrings() {\n" +
        "  println(\"\"\" { Inside a multiline string literal } \"\"\")\n" +
        "}"

    assertThat(parse(functionWithMultilineStringLiteral))
      .isEqualTo(WellFormedFunction.with(1, 3, 1))
  }

  @Test
  fun `it can detect malformed snippets`() {
    val malformedSnippet = """
      /* override func printAllThree<Z : MyPrintable>(t: Y, v: Z) {
        print("super ", terminator: "")
        super.printAllThree(t: t, v: v)
      } */
    }

    // CHECK: 1 two
    // CHECK: 1 two
    // CHECK: 1 two [3]
    OuterStruct<Int>.InnerStruct<String>(u: "two").printBoth(t: 1)
    OuterStruct<Int>.InnerStruct<String>.printBoth(t: 1, u: "two")
    OuterStruct<Int>.InnerStruct<String>(u: "two").printAllThree(t: 1, v: [3])
    
    // CHECK: override 1 two [3]
    SubClass<String, Int>(u: "two").printAllThree(t: 1, v: [3])
    """.trimIndent()

    assertThat(parse(malformedSnippet))
      .isEqualTo(MalformedFunction)
  }
}
