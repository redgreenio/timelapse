package io.redgreen.timelapse.devcli.commands.xd.html

import io.redgreen.design.text.StyledText
import io.redgreen.timelapse.devcli.commands.xd.html.visitors.BaseHtmlVisitor
import org.approvaltests.Approvals
import org.junit.jupiter.api.Test

class KotlinSyntaxHighlighterTest {
  @Test
  fun `it should highlight Kotlin source code`() {
    // given
    val sourceCode = KotlinSyntaxHighlighterTest::class.java.classLoader
      .getResourceAsStream("syntax-highlighting-01.kotlin")!!
      .reader()
      .readText()
      .replaceLineEndingOnWindows()

    val styledText = StyledText(sourceCode)

    val affectedLineNumbers = (1..23).toList()

    // when
    KotlinSyntaxHighlighter.addStylesForTokens(styledText, affectedLineNumbers)
    val visitor = BaseHtmlVisitor("Kotlin Source", affectedLineNumbers)
    styledText.visit(visitor)

    // then
    Approvals.verifyHtml(visitor.content)
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
