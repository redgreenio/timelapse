package io.redgreen.timelapse.devcli.commands.xd.html

import io.redgreen.design.text.StyledText
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.io.File

@Command(
  name = "xd-html",
  mixinStandardHelpOptions = true,
  description = ["creates a base HTML file for use with Xd"]
)
class CreateBaseHtmlSubcommand : Runnable {
  companion object {
    private const val COMMIT_HASH_RANGE = 8
    private const val EXTENSION_HTML = ".html"
    private const val DIRECTORY_XD_BASE_HTML = "xd-base-html"

    private const val PROPERTY_KEY_USER_HOME = "user.home"
  }

  @Parameters(index = "0", description = ["File name"])
  lateinit var fileName: String

  @Parameters(index = "1", description = ["Commit hash"])
  lateinit var commitHash: String

  @Option(names = ["-o", "--output"], description = ["Output directory path"])
  var outputDirectoryPath: File = File(System.getProperty(PROPERTY_KEY_USER_HOME)).resolve(DIRECTORY_XD_BASE_HTML)

  override fun run() {
    if (!outputDirectoryPath.exists()) {
      outputDirectoryPath.mkdirs()
    }
    val outputFile = outputDirectoryPath.resolve(baseHtmlFileName(commitHash, fileName))

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
    val range = 0..((commitHash.length - 1).coerceAtMost(COMMIT_HASH_RANGE))
    return "$fileName-${commitHash.substring(range)}$EXTENSION_HTML"
  }
}
