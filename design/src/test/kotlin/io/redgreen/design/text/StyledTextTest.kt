package io.redgreen.design.text

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

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
      // given
      val visitor = object : DefaultStyledTextVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          lineBuilder.append(lineNumber)
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(lineBuilder.toString())
        .isEqualTo("123")
    }

    @Test
    fun `it should provide callbacks while exiting a line`() {
      // given
      val visitor = object : DefaultStyledTextVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          lineBuilder.append("begin $lineNumber ")
        }

        override fun onExitLine(lineNumber: Int) {
          lineBuilder.append("end $lineNumber ")
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(lineBuilder.toString())
        .isEqualTo("begin 1 end 1 begin 2 end 2 begin 3 end 3 ")
    }

    @Test
    fun `it should provide callbacks with a line style when entering a line`() {
      // given
      styledText
        .addStyle(LineStyle("added", 1..3))

      val visitor = object : DefaultStyledTextVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          fail { "onEnterLine() should not be called when there's an associated `LineStyle`" }
        }

        override fun onEnterLine(lineNumber: Int, style: LineStyle) {
          lineBuilder.append(
            """
              <tr class="${style.name}"><td>$lineNumber</td></tr>
              
            """.trimIndent()
          )
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(lineBuilder.toString())
        .isEqualTo(
          """
            <tr class="added"><td>1</td></tr>
            <tr class="added"><td>2</td></tr>
            <tr class="added"><td>3</td></tr>
            
          """.trimIndent()
        )
    }
  }
}
