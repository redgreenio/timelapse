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
      // given
      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          lineBuilder.append(lineNumber)
        }

        override fun onText(text: String) {
          lineBuilder.append(text)
        }

        override fun onExitLine(lineNumber: Int) {
          /* no-op */
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(lineBuilder.toString())
        .isEqualTo("1One2Two3Three")
    }

    @Test
    fun `it should provide callbacks while exiting a line`() {
      // given
      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          lineBuilder.append("begin $lineNumber ")
        }

        override fun onText(text: String) {
          // no-op
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

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int, style: LineStyle) {
          lineBuilder.append(
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
      assertThat(lineBuilder.toString())
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
          lineBuilder.append(
            """
              <exit class="${style.name}">$lineNumber</exit>
              
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
      val lineStyleBuilder = StringBuilder()

      val text = """
        Hello, world!
        How are you?
      """.trimIndent()

      val styledText = StyledText(text)
        .addStyle(LineStyle("greeting", 1))
        .addStyle(LineStyle("question", 2))

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int, style: LineStyle) {
          lineStyleBuilder.append("""<tr><td class="${style.name}">$lineNumber</td>""")
        }

        override fun onText(text: String) {
          lineStyleBuilder.append("<td>$text</td>")
        }

        override fun onExitLine(lineNumber: Int, style: LineStyle) {
          lineStyleBuilder.append("</tr>\n")
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(lineStyleBuilder.toString())
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
      val textBuilder = StringBuilder()

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
          textBuilder.append("\n")
        }

        override fun onText(text: String) {
          textBuilder.append(text)
        }
      }

      // when
      StyledText(text).visit(visitor)

      // then
      assertThat(textBuilder.toString())
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
      val textBuilder = StringBuilder()
      val text = "Hello, Oreo!"

      val styledText = StyledText(text)
        .addStyle(TextStyle("bold", 7..10))

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          // no-op
        }

        override fun onText(text: String) {
          textBuilder.append(text)
        }

        override fun onText(text: String, textStyle: TextStyle) {
          textBuilder.append("<${textStyle.name}>$text</${textStyle.name}>")
        }

        override fun onExitLine(lineNumber: Int) {
          // no-op
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(textBuilder.toString())
        .isEqualTo("Hello, <bold>Oreo</bold>!")
    }

    @Test
    fun `it should apply more than one style on the same line`() {
      // given
      val textBuilder = StringBuilder()
      val text = "HelloWorld"

      val styledText = StyledText(text)
        .addStyle(TextStyle("bold", 0..4))
        .addStyle(TextStyle("em", 5..9))

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          // no-op
        }

        override fun onText(text: String, textStyle: TextStyle) {
          textBuilder.append("<${textStyle.name}>$text</${textStyle.name}>")
        }

        override fun onExitLine(lineNumber: Int) {
          // no-op
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(textBuilder.toString())
        .isEqualTo("<bold>Hello</bold><em>World</em>")
    }
  }
}
