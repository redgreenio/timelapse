package io.redgreen.timelapse.devcli.commands.xd

import io.redgreen.design.text.StyledText
import io.redgreen.timelapse.devcli.commands.xd.html.visitors.BaseHtmlVisitor
import org.approvaltests.Approvals
import org.approvaltests.core.Options
import org.approvaltests.scrubbers.RegExScrubber
import org.junit.jupiter.api.Test

class BaseHtmlVisitorTest {
  private val toolVersionScrubber = RegExScrubber(
    "<!--Generated by dev-cli: v\\d+.\\d+.\\d+(-\\w+)?-->",
    "<!--Generated by dev-cli: v[tool-version]-->"
  )
  private val scrubberOptions = Options(toolVersionScrubber)

  @Test
  fun `it should generate a base HTML for Xd experimentation`() {
    // given
    val kotlinSource = """
      fun add(x: Int, y: Int) {
        return x + y
      }
      
    """.trimIndent()
    val styledText = StyledText(kotlinSource)
    val visitor = BaseHtmlVisitor("Untitled")

    // when
    styledText.visit(visitor)

    // then
    Approvals.verifyHtml(visitor.content, scrubberOptions)
  }

  @Test
  fun `it should highlight affected lines in a commit`() {
    // given
    val kotlinSource = """
      fun add(x: Int, y: Int) {
        return x + y
      }
      
    """.trimIndent()
    val styledText = StyledText(kotlinSource)
    val visitor = BaseHtmlVisitor("Untitled", listOf(2))

    // when
    styledText.visit(visitor)

    // then
    Approvals.verifyHtml(visitor.content, scrubberOptions)
  }
}
