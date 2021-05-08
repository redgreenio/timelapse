package io.redgreen.timelapse.devcli.commands.xd

import io.redgreen.design.text.StyledText
import io.redgreen.timelapse.devcli.commands.xd.html.BaseHtmlVisitor
import org.approvaltests.Approvals
import org.junit.jupiter.api.Test

class BaseHtmlVisitorTest {
  @Test
  fun `it should generate a base HTML for Xd experimentation`() {
    // given
    val kotlinSource = """
      fun add(x: Int, y: Int) {
        return x + y
      }
      
    """.trimIndent()
    val styledText = StyledText(kotlinSource)
    val visitor = BaseHtmlVisitor()

    // when
    styledText.visit(visitor)

    // then
    Approvals.verifyHtml(visitor.content)
  }
}
