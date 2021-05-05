package io.redgreen.design.text

import com.google.common.truth.Truth.assertThat
import org.approvaltests.Approvals
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
        override fun onExitLine(lineNumber: Int) {}

        override fun onEnterLine(lineNumber: Int) {
          contentBuilder.append(lineNumber)
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
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
        override fun onText(text: String) {}

        override fun onEnterLine(lineNumber: Int) {
          contentBuilder.append("begin $lineNumber ")
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
        override fun onText(text: String) {}
        override fun onExitLine(lineNumber: Int, style: LineStyle) {}

        override fun onEnterLine(lineNumber: Int, style: LineStyle) {
          if (lineNumber != 1) {
            contentBuilder.append("\n")
          }
          contentBuilder.append("""<tr class="${style.name}"><td>$lineNumber</td></tr>""")
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
        override fun onText(text: String) {}

        override fun onEnterLine(lineNumber: Int, style: LineStyle) {
          if (lineNumber == 1) return

          contentBuilder.append("\n")
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
  inner class LineStyleCallbacks {
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
          if (lineNumber != 1) {
            contentBuilder.append("\n")
          }
          contentBuilder.append("""<tr><td class="${style.name}">$lineNumber</td>""")
        }

        override fun onText(text: String) {
          contentBuilder.append("<td>$text</td>")
        }

        override fun onExitLine(lineNumber: Int, style: LineStyle) {
          contentBuilder.append("</tr>")
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
  inner class TextStyleCallbacks {
    @Test
    fun `it should receive callbacks for text without style`() {
      // given

      val text = """
        fun helloWorld() {
          println("Hello, world!")
        }
      """.trimIndent()

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onExitLine(lineNumber: Int) {}

        override fun onEnterLine(lineNumber: Int) {
          if (lineNumber == 1) return

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
        override fun onEnterLine(lineNumber: Int) {}
        override fun onExitLine(lineNumber: Int) {}

        override fun onBeginStyle(textStyle: TextStyle) {
          contentBuilder.append("<${textStyle.name}>")
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
        }

        override fun onEndStyle(textStyle: TextStyle) {
          contentBuilder.append("</${textStyle.name}>")
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
        override fun onEnterLine(lineNumber: Int) {}
        override fun onExitLine(lineNumber: Int) {}

        override fun onBeginStyle(textStyle: TextStyle) {
          contentBuilder.append("<${textStyle.name}>")
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
        }

        override fun onEndStyle(textStyle: TextStyle) {
          contentBuilder.append("</${textStyle.name}>")
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
        override fun onExitLine(lineNumber: Int) {}

        override fun onEnterLine(lineNumber: Int) {
          if (lineNumber == 1) return
          contentBuilder.append("\n")
        }

        override fun onBeginStyle(textStyle: TextStyle) {
          contentBuilder.append("""<span class="${textStyle.name}">""")
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
        }

        override fun onEndStyle(textStyle: TextStyle) {
          contentBuilder.append("""</span>""")
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

    @Test
    fun `it should add styles to single characters`() {
      // given
      val text = "abc"
      val styledText = StyledText(text)
        .addStyle(TextStyle("brackets", 1, 1))

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {}
        override fun onExitLine(lineNumber: Int) {}

        override fun onBeginStyle(textStyle: TextStyle) {
          contentBuilder.append("[")
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
        }

        override fun onEndStyle(textStyle: TextStyle) {
          contentBuilder.append("]")
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo("a[b]c")
    }

    @Test
    fun `fun with syntax highlighting`() {
      // given
      val text = """
        private fun helloWorld() {
          println("Hello, world!")
        }
      """.trimIndent()

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {
          if (lineNumber != 1) {
            contentBuilder.append("\n")
          }
          contentBuilder.append("<tr><td>")
        }

        override fun onExitLine(lineNumber: Int) {
          contentBuilder.append("</td></tr>")
        }

        override fun onBeginStyle(textStyle: TextStyle) {
          contentBuilder.append("""<span class="${textStyle.name}">""")
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
        }

        override fun onEndStyle(textStyle: TextStyle) {
          contentBuilder.append("</span>")
        }
      }

      val styledText = StyledText(text)
        .addStyle(TextStyle("keyword", 1, 0..6))
        .addStyle(TextStyle("keyword", 1, 8..10))
        .addStyle(TextStyle("function-name", 1, 12..21))
        .addStyle(TextStyle("lpar", 1, 22))
        .addStyle(TextStyle("rpar", 1, 23))
        .addStyle(TextStyle("function-call", 2, 2..8))
        .addStyle(TextStyle("lpar", 2, 9))
        .addStyle(TextStyle("string", 2, 10..24))
        .addStyle(TextStyle("rpar", 2, 25))
        .addStyle(TextStyle("lcurly", 1, 25))
        .addStyle(TextStyle("rcurly", 3, 0))

      // when
      styledText.visit(visitor)

      // then
      Approvals.verify(visitor.content)
    }
  }

  @Nested
  inner class OverlappingTextStyles {
    // Case: Subset overlap
    // ----------------------------------
    // Hello, world!
    // ^           ^ <== bold
    //        ^   ^  <== italic
    // ----------------------------------
    // <b>Hello, <i>world</i>!</b>
    @Test
    fun `it should handle subset overlap`() {
      // given
      val text = "Hello, world!"

      val styledText = StyledText(text)
        .addStyle(TextStyle("b", 1, 0..12))
        .addStyle(TextStyle("i", 1, 7..11))

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {}
        override fun onExitLine(lineNumber: Int) {}

        override fun onBeginStyle(textStyle: TextStyle) {
          contentBuilder.append("<${textStyle.name}>")
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
        }

        override fun onEndStyle(textStyle: TextStyle) {
          contentBuilder.append("</${textStyle.name}>")
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo("<b>Hello, <i>world</i>!</b>")
    }

    // Case: Intersecting overlap
    // ----------------------------------
    // Hello, world!
    //   ^     ^     <== bold
    //      ^     ^  <== italic
    // ----------------------------------
    // He<b>llo<i>, wo</b>rld</i>!
    @Test
    fun `it should handle intersecting overlap`() {
      // given
      val text = "Hello, world!"

      val styledText = StyledText(text)
        .addStyle(TextStyle("b", 1, 2..8))
        .addStyle(TextStyle("i", 1, 5..11))

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {}
        override fun onExitLine(lineNumber: Int) {}

        override fun onBeginStyle(textStyle: TextStyle) {
          contentBuilder.append("<${textStyle.name}>")
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
        }

        override fun onEndStyle(textStyle: TextStyle) {
          contentBuilder.append("</${textStyle.name}>")
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo("He<b>llo<i>, wo</b>rld</i>!")
    }

    // Case: Incomplete overlap (start)
    // ----------------------------------
    // Hello, world!
    // ^           ^ <== bold
    // ^   ^         <== italic
    // ----------------------------------
    // <b><i>Hello</i>, world!</b>
    @Test
    fun `it should handle overlapping start indices`() {
      // given
      val text = "Hello, world!"

      val styledText = StyledText(text)
        .addStyle(TextStyle("b", 1, 0..12))
        .addStyle(TextStyle("i", 1, 0..4))

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {}
        override fun onExitLine(lineNumber: Int) {}

        override fun onBeginStyle(textStyle: TextStyle) {
          contentBuilder.append("<${textStyle.name}>")
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
        }

        override fun onEndStyle(textStyle: TextStyle) {
          contentBuilder.append("</${textStyle.name}>")
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo("<b><i>Hello</i>, world!</b>")
    }

    // Case: Complete overlap
    // ----------------------------------
    // Hello, world!
    // ^           ^ <== bold
    // ^           ^ <== italic
    // ----------------------------------
    // <b><i>Hello, world!</i></b>
    @Test
    fun `it should handle completely overlapping styles`() {
      // given
      val text = "Hello, world!"

      val styledText = StyledText(text)
        .addStyle(TextStyle("b", 1, 0..12))
        .addStyle(TextStyle("i", 1, 0..12))

      val visitor = object : CrashAndBurnOnUnexpectedCallbackVisitor() {
        override fun onEnterLine(lineNumber: Int) {}
        override fun onExitLine(lineNumber: Int) {}

        override fun onBeginStyle(textStyle: TextStyle) {
          contentBuilder.append("<${textStyle.name}>")
        }

        override fun onText(text: String) {
          contentBuilder.append(text)
        }

        override fun onEndStyle(textStyle: TextStyle) {
          contentBuilder.append("</${textStyle.name}>")
        }
      }

      // when
      styledText.visit(visitor)

      // then
      assertThat(visitor.content)
        .isEqualTo("<b><i>Hello, world!</i></b>")
    }
  }
}
