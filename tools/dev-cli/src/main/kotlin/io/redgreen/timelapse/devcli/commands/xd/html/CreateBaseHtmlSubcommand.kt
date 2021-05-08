package io.redgreen.timelapse.devcli.commands.xd.html

import io.redgreen.design.text.StyledText
import picocli.CommandLine.Command
import java.io.File

@Command(
  name = "xd-html",
  mixinStandardHelpOptions = true,
  description = ["creates a base HTML file for use with Xd"]
)
class CreateBaseHtmlSubcommand : Runnable {
  companion object {
    private val COMMIT_HASH_RANGE = 0..7
    private const val EXTENSION_HTML = ".html"
  }

  override fun run() {
    val fileName = "ExtendedDiffHtml.kt"
    val commitHash = "91bf2ea245bda89ace754e188243ebde7cf55e47"
    val outputFile = File("/Users/ragunathjawahar/Desktop/html/${baseHtmlFileName(commitHash, fileName)}")

    val filePath = GitCommand.LsFiles.from(fileName).execute()
    val fileContent = GitCommand.Show.from(commitHash, filePath).execute()
    outputFile.writeText(getHtml(fileContent))

    println("Base HTML file written to: ${outputFile.canonicalPath}")
  }

  private fun getHtml(fileContent: String): String {
    val visitor = BaseHtmlVisitor()
    StyledText(fileContent).visit(visitor)
    return visitor.content
  }

  private fun baseHtmlFileName(commitHash: String, fileName: String): String {
    return "$fileName-${commitHash.substring(COMMIT_HASH_RANGE)}$EXTENSION_HTML"
  }
}
