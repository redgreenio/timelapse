package io.redgreen.design.text

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import java.lang.StringBuilder

class StyledTextTest {
  @Test
  fun `it should provide callbacks while entering a new line`() {
    // given
    val text = """
      One
      Two
      Three
    """.trimIndent()

    val styledText = StyledText(text)

    val lineBuilder = StringBuilder()

    // when
    styledText.visit { lineNumber -> lineBuilder.append(lineNumber) }

    // then
    assertThat(lineBuilder.toString())
      .isEqualTo("123")
  }
}
