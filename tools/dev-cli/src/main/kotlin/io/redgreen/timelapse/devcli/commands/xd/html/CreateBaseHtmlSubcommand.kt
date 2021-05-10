package io.redgreen.timelapse.devcli.commands.xd.html

import io.redgreen.design.text.StyledText
import org.fusesource.jansi.Ansi.ansi
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.io.File

@Command(
  name = "html",
  mixinStandardHelpOptions = true,
  description = ["creates a base HTML file for use with XD"]
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
    val outputFile = outputDirectoryPath.resolve(baseHtmlFileName(fileName, commitHash))

    val filePath = GitCommand.LsFiles.from(fileName).execute().output
    val content = GitCommand.Show.from(commitHash, filePath).execute().output
    outputFile.writeText(getHtml("$fileName @ ${shortCommitHash(commitHash)}", content))

    val message = ansi()
      .render("@|green Base HTML file written to:|@\n@|bold ${outputFile.canonicalPath}|@")
    println(message)
  }

  private fun getHtml(title: String, content: String): String {
    val visitor = BaseHtmlVisitor(title)
    StyledText(content).visit(visitor)
    return visitor.content
  }

  private fun baseHtmlFileName(fileName: String, commitHash: String): String {
    return "$fileName-${shortCommitHash(commitHash)}$EXTENSION_HTML"
  }

  private fun shortCommitHash(commitHash: String): String {
    val range = 0..((commitHash.length - 1).coerceAtMost(COMMIT_HASH_RANGE))
    return commitHash.substring(range)
  }
}
