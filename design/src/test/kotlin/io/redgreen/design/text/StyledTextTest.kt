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

    @Test
    fun `it should provide callbacks while entering a new line`() {
      // given
      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          contentBuilder.append(lineNumber)
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
        }

        override fun onExitLine(lineNumber: Int) {
          /* no-op */
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo("1One2Two3Three")
    }

    @Test
    fun `it should provide callbacks while exiting a line`() {
      // given
      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          contentBuilder.append("begin $lineNumber ")
        }

        override fun onText(text: String) {
          // no-op
        }

        override fun onExitLine(lineNumber: Int) {
          contentBuilder.append("end $lineNumber ")
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo("begin 1 end 1 begin 2 end 2 begin 3 end 3 ")
    }

    @Test
    fun `it should provide callbacks with a line style when entering a line`() {
      // given
      styledText
        .addStyle(LineStyle("added", 1..3))

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int, style: LineStyle) {
          contentBuilder.append(
            """
              <tr class="${style.name}"><td>$lineNumber</td></tr>
              
            """.trimIndent()
          )
        }

        override fun onText(text: String) {
          // no-op
        }

        override fun onExitLine(lineNumber: Int, style: LineStyle) {
          /* no-op */
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo(
          """
            <tr class="added"><td>1</td></tr>
            <tr class="added"><td>2</td></tr>
            <tr class="added"><td>3</td></tr>
            
          """.trimIndent()
        )
    }

    @Test
    fun `it should provide callbacks with a line style when exiting a line`() {
      // given
      styledText
        .addStyle(LineStyle("added", 1..3))

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int, style: LineStyle) {
          /* no-op */
        }

        override fun onText(text: String) {
          // no-op
        }

        override fun onExitLine(lineNumber: Int, style: LineStyle) {
          contentBuilder.append(
            """
              <exit class="${style.name}">$lineNumber</exit>
              
            """.trimIndent()
          )
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo(
          """
            <exit class="added">1</exit>
            <exit class="added">2</exit>
            <exit class="added">3</exit>
            
          """.trimIndent()
        )
    }
  }

  @Nested
  inner class LineStyles {
    @Test
    fun `it should be able to handle different line styles for different lines`() {
      // given

      val text = """
        Hello, world!
        How are you?
      """.trimIndent()

      val styledText = StyledText(text)
        .addStyle(LineStyle("greeting", 1))
        .addStyle(LineStyle("question", 2))

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int, style: LineStyle) {
          contentBuilder.append("""<tr><td class="${style.name}">$lineNumber</td>""")
        }

        override fun onText(text: String) {
          contentBuilder.append("<td>$text</td>")
        }

        override fun onExitLine(lineNumber: Int, style: LineStyle) {
          contentBuilder.append("</tr>\n")
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo(
          """
            <tr><td class="greeting">1</td><td>Hello, world!</td></tr>
            <tr><td class="question">2</td><td>How are you?</td></tr>
            
          """.trimIndent()
        )
    }
  }

  @Nested
  inner class Text {
    @Test
    fun `it should receive callbacks for text without style`() {
      // given

      val text = """
        fun helloWorld() {
          println("Hello, world!")
        }
      """.trimIndent()

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          // no-op
        }

        override fun onExitLine(lineNumber: Int) {
          contentBuilder.append("\n")
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
        }
      }

      // when
      StyledText(text).visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo(
          """
          fun helloWorld() {
            println("Hello, world!")
          }
          
          """.trimIndent()
        )
    }

    @Test
    fun `it should receive callback for text with style`() {
      // given
      val text = "Hello, Oreo!"

      val styledText = StyledText(text)
        .addStyle(TextStyle("bold", 1, 7..10))

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          // no-op
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
        }

        override fun onText(text: String, textStyle: TextStyle) {
          contentBuilder.append("<${textStyle.name}>$text</${textStyle.name}>")
        }

        override fun onExitLine(lineNumber: Int) {
          // no-op
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo("Hello, <bold>Oreo</bold>!")
    }

    @Test
    fun `it should apply more than one style on the same line`() {
      // given
      val text = "HelloWorld"

      val styledText = StyledText(text)
        .addStyle(TextStyle("bold", 1, 0..4))
        .addStyle(TextStyle("em", 1, 5..9))

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          // no-op
        }

        override fun onText(text: String, textStyle: TextStyle) {
          contentBuilder.append("<${textStyle.name}>$text</${textStyle.name}>")
        }

        override fun onExitLine(lineNumber: Int) {
          // no-op
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo("<bold>Hello</bold><em>World</em>")
    }

    @Test
    fun `it should apply multiple styles on multiple lines`() {
      // given
      val text = """
        fun helloWorld() {
          println("Hello, world!")
        }
      """.trimIndent()

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          // no-op
        }

        override fun onExitLine(lineNumber: Int) {
          contentBuilder.append("\n")
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
        }

        override fun onText(text: String, textStyle: TextStyle) {
          contentBuilder.append("""<span class="${textStyle.name}">$text</span>""")
        }
      }

      val styledText = StyledText(text)
        .addStyle(TextStyle("identifier", 1, 4..13))
        .addStyle(TextStyle("diff", 2, 11..23))

      // when
      styledText.visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo(
          """
            fun <span class="identifier">helloWorld</span>() {
              println("<span class="diff">Hello, world!</span>")
            }
            
          """.trimIndent()
        )
    }
  }
}
