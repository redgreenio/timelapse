package io.redgreen.timelapse.devcli.commands.xd.html

import io.redgreen.design.text.StyledText
import picocli.CommandLine.Command

@Command(
  name = "xd-html",
  mixinStandardHelpOptions = true,
  description = ["creates a base HTML file for use with Xd"]
)
class CreateBaseHtmlSubcommand : Runnable {
  override fun run() {
    val fileName = "ExtendedDiffHtml.kt"
    val commitHash = "1f69e11d280dc95d6563a504f11be75766273236"

    val filePath = GitCommand.LsFiles.from(fileName).execute()
    val fileContent = GitCommand.Show.from(commitHash, filePath).execute()

    println(getHtml(fileContent))
  }

  private fun getHtml(fileContent: String): String {
    val visitor = BaseHtmlVisitor()
    StyledText(fileContent).visit(visitor)
    return visitor.content
  }
}
