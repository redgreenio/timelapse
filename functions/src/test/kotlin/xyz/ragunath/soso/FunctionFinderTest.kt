package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.Test

// TODO Run an accuracy test for detection for each language
class FunctionFinderTest {
  // TODO Test for no function

  @Test
  fun `it can return the start and end row of a function declaration`() {
    val singleLineFunction = """fun main() { println("Ta da!") }"""

    assertThat(findFunction(singleLineFunction))
      .isEqualTo(Location(1, 1))
  }

  @Test
  fun `it can return the start and end row of a function preceded by non-function lines`() {
    val singleLineFunction = """
      package xyz.ragunath.foo

      // This file contains a main function.

      fun main() { println("Bang bang!") }
      """.trimIndent()

    assertThat(findFunction(singleLineFunction))
      .isEqualTo(Location(5, 5))
  }
}
