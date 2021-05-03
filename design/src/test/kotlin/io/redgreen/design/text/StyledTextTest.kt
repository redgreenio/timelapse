package io.redgreen.design.text

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StyledTextTest {
  @Nested
  inner class NewlineCallbacks {
    private val text = """
      One
      Two
      Three
    """.trimIndent()
    private val styledText = StyledText(text)
    private val lineBuilder = StringBuilder()

    @Test
    fun `it should provide callbacks while entering a new line`() {
      // when
      styledText.visit(onEnterLine = { lineNumber -> lineBuilder.append(lineNumber) })

      // then
      assertThat(lineBuilder.toString())
        .isEqualTo("123")
    }

    @Test
    fun `it should provide callbacks  while exiting a line`() {
      // when
      styledText.visit(
        onEnterLine = { lineNumber -> lineBuilder.append("begin $lineNumber ") },
        onExitLine = { lineNumber -> lineBuilder.append("end $lineNumber ") }
      )

      // then
      assertThat(lineBuilder.toString())
        .isEqualTo("begin 1 end 1 begin 2 end 2 begin 3 end 3 ")
    }
  }
}
