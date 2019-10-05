package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SoSoTest {
  @Test
  fun `when there are no brackets, then return a depth of 0`() {
    val noBrackets = ""
    assertThat(SoSo.analyze(noBrackets).depth)
      .isEqualTo(0)
  }

  @Test
  fun `when there are a pair of brackets, then return a depth of 1 (single line)`() {
    val onePairOfBracketsSingleLine = "{}"
    assertThat(SoSo.analyze(onePairOfBracketsSingleLine).depth)
      .isEqualTo(1)
  }

  @Test
  fun `when there are two pairs of brackets, then return a depth of 2 (single line)`() {
    val twoPairsOfBracketsSingleLine = "{{}}"
    assertThat(SoSo.analyze(twoPairsOfBracketsSingleLine).depth)
      .isEqualTo(2)
  }

  @Test
  fun `when there are a pair of brackets on different lines, then return a depth of 1`() {
    val onePairOfBracketDifferentLines = """
        {
        }
      """.trimIndent()

    assertThat(SoSo.analyze(onePairOfBracketDifferentLines).depth)
      .isEqualTo(1)
  }

  @Test
  fun `when there is a Kotlin top-level function, then report a depth of 1`() {
    val mainFunction = """
      fun main() {
      }
    """.trimIndent()

    assertThat(SoSo.analyze(mainFunction).depth)
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

    assertThat(SoSo.analyze(functionWithConditional).depth)
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

    assertThat(SoSo.analyze(functionWithIfElseLadder).depth)
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

    assertThat(SoSo.analyze(functionWith4LevelsOfNesting).depth)
      .isEqualTo(4 + 1)
  }
}
