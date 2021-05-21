package io.redgreen.timelapse.devcli.commands.xd.html

import io.redgreen.design.text.StyledText
import io.redgreen.timelapse.devcli.commands.xd.html.visitors.BaseHtmlVisitor
import org.approvaltests.Approvals
import org.junit.jupiter.api.Test

class KotlinSyntaxHighlighterTest {
  @Test
  fun `it should highlight Kotlin source code`() {
    // given
    val sourceCode = """
      package io.redgreen.timelapse.devcli.commands.xd.html

      sealed class GitCommand {
        object LsFiles : GitCommand() {
          fun command(fileName: String): ShellCommand =
            ShellCommand(arrayOf("git", "ls-files", fileName, "**/${'$'}fileName"))
        }

        object Show : GitCommand() {
          fun command(commitHash: String, filePath: String): ShellCommand =
            ShellCommand(arrayOf("git", "show", "${'$'}commitHash:${'$'}filePath"))
        }

        object Status : GitCommand() {
          fun command(): ShellCommand =
            ShellCommand(arrayOf("git", "status"))
        }

        object GetUnifiedPatch : GitCommand() {
          fun command(commitHash: String, filePath: String): ShellCommand =
            ShellCommand(arrayOf("git", "diff", "-u", "${'$'}commitHash^1", commitHash, "--", filePath))
        }
      }
    """.trimIndent()

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
