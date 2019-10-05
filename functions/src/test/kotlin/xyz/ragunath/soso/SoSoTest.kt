package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SoSoTest {
  @Test
  fun `when there are no brackets, then return a depth of 0`() {
    val noBrackets = ""
    assertThat(SoSo.depthOf(noBrackets))
      .isEqualTo(0)
  }

  @Test
  fun `when there are a pair of brackets, then return a depth of 1 (single line)`() {
    val onePairOfBracketsSingleLine = "{}"
    assertThat(SoSo.depthOf(onePairOfBracketsSingleLine))
      .isEqualTo(1)
  }

  @Test
  fun `when there are two pairs of brackets, then return a depth of 2 (single line)`() {
    val twoPairsOfBracketsSingleLine = "{{}}"
    assertThat(SoSo.depthOf(twoPairsOfBracketsSingleLine))
      .isEqualTo(2)
  }

  @Test
  fun `when there are a pair of brackets on different lines, then return a depth of 1`() {
    val onePairOfBracketDifferentLines = """
        {
        }
      """.trimIndent()

    assertThat(SoSo.depthOf(onePairOfBracketDifferentLines))
      .isEqualTo(1)
  }

  @Test
  fun `when there is a Kotlin top-level function, then report a depth of 1`() {
    val mainFunction = """
      fun main() {
      }
    """.trimIndent()

    assertThat(SoSo.depthOf(mainFunction))
      .isEqualTo(1)
  }

  @Test
  fun `when there is a Kotlin top-level function with a conditional, then report a depth of 2`() {
    val functionWithConditional = """
      fun main() {
        if (true) {
          // Doesn't matter
        }
      }
    """.trimIndent()

    assertThat(SoSo.depthOf(functionWithConditional))
      .isEqualTo(2)
  }

  @Test
  fun `when there is a Kotlin function with an if else ladder, then report a depth of 2`() {
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

    assertThat(SoSo.depthOf(functionWithIfElseLadder))
      .isEqualTo(2)
  }

  @Test
  fun `when a kotlin function body has 4 nested if statements, then report a depth of 4 + 1`() {
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

    assertThat(SoSo.depthOf(functionWith4LevelsOfNesting))
      .isEqualTo(4 + 1)
  }
}
