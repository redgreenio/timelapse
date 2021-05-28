package io.redgreen.timelapse.devcli.commands.xd.html

import io.redgreen.design.text.StyledText
import io.redgreen.timelapse.devcli.commands.xd.html.visitors.BaseHtmlVisitor
import org.approvaltests.Approvals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class KotlinStylerTest {
  @Nested
  inner class SyntaxHighlight {
    @Test
    fun `it should highlight Kotlin source code`() {
      // given
      val sourceCode = KotlinStylerTest::class.java.classLoader
        .getResourceAsStream("syntax-highlighting-01.kotlin")!!
        .reader()
        .readText()
        .replaceLineEndingOnWindows()

      val styledText = StyledText(sourceCode)

      val affectedLineNumbers = (1..23).toList()

      // when
      KotlinStyler.syntaxHighlight(styledText, affectedLineNumbers)
      val visitor = BaseHtmlVisitor("Kotlin Source", affectedLineNumbers)
      styledText.visit(visitor)

      // then
      Approvals.verifyHtml(visitor.content, scrubberOptions)
    }

    @Test
    fun `it should highlight all kinds of brackets`() {
      // given
      val sourceCode = KotlinStylerTest::class.java.classLoader
        .getResourceAsStream("syntax-highlighting-02.kotlin")!!
        .reader()
        .readText()
        .replaceLineEndingOnWindows()

      val styledText = StyledText(sourceCode)

      val affectedLineNumbers = (1..26).toList()

      // when
      KotlinStyler.syntaxHighlight(styledText, affectedLineNumbers)
      val visitor = BaseHtmlVisitor("Kotlin Source", affectedLineNumbers)
      styledText.visit(visitor)

      // then
      Approvals.verifyHtml(visitor.content, scrubberOptions)
    }
  }

  @Nested
  inner class LanguageSemantics {
    @Test
    fun `it should mark function boundaries`() {
      // given
      val singleFunction = """
        fun add(a: Int, b: Int): Int {
          return a + b
        }

        fun multiply(a: Int, b: Int): Int {
          return a * b
        }
        
      """.trimIndent()

      val styledText = StyledText(singleFunction)

      // when
      KotlinStyler.addLanguageSemantics(styledText)
      val visitor = BaseHtmlVisitor("Kotlin Source", emptyList())
      styledText.visit(visitor)

      // then
      Approvals.verifyHtml(visitor.content, scrubberOptions)
    }
  }
}

private fun String.replaceLineEndingOnWindows(): String {
  val isWindows = System.getProperty("os.name").toLowerCase().contains("windows")
  return if (isWindows) {
    this.replace("\r\n", "\n")
  } else {
    this
  }
}
