package io.redgreen.timelapse.devcli.commands.xd.html

import KotlinLexer
import KotlinLexer.ABSTRACT
import KotlinLexer.AS
import KotlinLexer.CLASS
import KotlinLexer.CONST
import KotlinLexer.DATA
import KotlinLexer.ELSE
import KotlinLexer.FUN
import KotlinLexer.GETTER
import KotlinLexer.IF
import KotlinLexer.IMPORT
import KotlinLexer.IN
import KotlinLexer.IS
import KotlinLexer.NullLiteral
import KotlinLexer.OBJECT
import KotlinLexer.OVERRIDE
import KotlinLexer.PACKAGE
import KotlinLexer.PRIVATE
import KotlinLexer.RETURN
import KotlinLexer.SEALED
import KotlinLexer.THIS
import KotlinLexer.VAL
import KotlinLexer.WHEN
import io.redgreen.design.text.StyledText
import io.redgreen.design.text.TextStyle
import io.redgreen.timelapse.devcli.commands.xd.html.ExecutionResult.Failure
import io.redgreen.timelapse.devcli.commands.xd.html.ExecutionResult.Success
import io.redgreen.timelapse.devcli.commands.xd.html.GitCommand.GetUnifiedPatch
import io.redgreen.timelapse.git.model.PatchFile
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
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
    private const val CHAR_NEWLINE = "\n"

    private const val PROPERTY_KEY_USER_HOME = "user.home"
  }

  @Parameters(index = "0", description = ["File name"])
  lateinit var fileName: String

  @Parameters(index = "1", description = ["Commit hash"])
  lateinit var commitHash: String

  @Option(names = ["-o", "--output"], description = ["Output directory path"])
  var outputDirectoryPath: File = File(System.getProperty(PROPERTY_KEY_USER_HOME)).resolve(DIRECTORY_XD_BASE_HTML)

  @Option(names = ["-d", "--debug"], description = ["Print debug logs"])
  var debug: Boolean = false

  override fun run() {
    val statusCommand = GitCommand.Status.command()
    statusCommand.log()
    val statusResult = statusCommand.execute()
    if (statusResult is Failure) {
      debug("Not inside a git repository.")
      println(ansi().render("@|red ${statusResult.output} |@"))
      return
    }
    debug("Inside git repository.")

    if (!outputDirectoryPath.exists()) {
      debug("Output directory: '${outputDirectoryPath.canonicalPath}' does not exist, creating...")
      outputDirectoryPath.mkdirs()
    } else {
      debug("Output directory: '${outputDirectoryPath.canonicalPath}' exists!")
    }

    debug("Looking for '$fileName' in repository...")
    val lsFilesCommand = GitCommand.LsFiles.command(fileName)
    lsFilesCommand.log()
    val lsFilesResult = lsFilesCommand.execute()
    val matchingFilePaths = lsFilesResult.output.split(CHAR_NEWLINE).filter(String::isNotBlank)
    val filePathInRepository = matchingFilePaths.first()
    val fileFound = lsFilesResult is Success && filePathInRepository.isNotEmpty()
    if (fileFound) {
      if (matchingFilePaths.size > 1) {
        println(ansi().fgYellow().render("Multiple matching file paths found, using the first one."))
        val numberedFilePaths = matchingFilePaths
          .mapIndexed { index, filePath -> "${index + 1}) $filePath" }
          .joinToString(CHAR_NEWLINE)
        println(ansi().fgDefault().render(numberedFilePaths))
        println()
      }

      debug("'$fileName' found at '$filePathInRepository'")
      val outputFile = outputDirectoryPath.resolve(baseHtmlFileName(fileName, commitHash))
      getFileContent(outputFile, filePathInRepository, commitHash)
    } else {
      debug("No match file found for '$fileName'.")
      println(ansi().fgRed().a("File not found: ").bold().a(fileName))
    }
  }

  private fun getFileContent(outputFile: File, filePath: String, commitHash: String) {
    val showCommand = GitCommand.Show.command(commitHash, filePath)
    showCommand.log()
    when (val showResult = showCommand.execute()) {
      is Success -> {
        debug("Retrieved file content at revision: $commitHash")
        debug("Writing file content to disk: ${outputFile.canonicalPath}")

        val getUnifiedPatchCommand = GetUnifiedPatch.command(commitHash, filePath)
        getUnifiedPatchCommand.log()
        val unifiedPatchExecutionResult = getUnifiedPatchCommand.execute()
        val affectedLineNumbers = if (unifiedPatchExecutionResult is Success) {
          debug("Getting affected line numbers from patch.")
          PatchFile.from(unifiedPatchExecutionResult.output).getAffectedLineNumbers()
        } else {
          debug("Unable to get affected line numbers from patch.")
          emptyList()
        }
        debug("Affected line numbers: $affectedLineNumbers")

        val html = getHtml("$fileName @ ${shortCommitHash(commitHash)}", showResult.output, affectedLineNumbers)
        outputFile.writeText(html)
        val message = ansi().render("@|green Base HTML file written to:|@\n@|bold ${outputFile.canonicalPath}|@")
        println(message)
      }

      is Failure -> {
        debug("Unable to retrieve file content at revision: $commitHash")
        val message = ansi().render("@|red ${showResult.output} |@")
        println(message)
      }
    }
  }

  private fun getHtml(
    title: String,
    content: String,
    affectedLineNumbers: List<Int>
  ): String {
    val visitor = BaseHtmlVisitor(title, affectedLineNumbers)
    val styledText = addSyntaxHighlighting(content, affectedLineNumbers)
    styledText.visit(visitor)
    return visitor.content
  }

  private fun addSyntaxHighlighting(
    content: String,
    affectedLineNumbers: List<Int>
  ): StyledText {
    val styledText = StyledText(content)
    addStylesForTokens(styledText, affectedLineNumbers)
    return styledText
  }

  private fun addStylesForTokens(
    outStyledText: StyledText,
    affectedLineNumbers: List<Int>
  ) {
    val charStream = CharStreams.fromString(outStyledText.text)
    val kotlinLexer = KotlinLexer(charStream)
    val commonTokenStream = CommonTokenStream(kotlinLexer).apply { numberOfOnChannelTokens }
    commonTokenStream
      .tokens
      .filter { it.line in affectedLineNumbers }
      .filter { isKeyword(it.type) }
      .onEach {
        if (isKeyword(it.type)) {
          val startIndex = it.charPositionInLine
          val stopIndex = startIndex + it.text.length
          outStyledText.addStyle(TextStyle("keyword", it.line, startIndex..stopIndex))
        }
      }
      .onEach { debug("${it.line}: ${it.type}: ${it.text}") }
  }

  private fun isKeyword(tokenType: Int): Boolean {
    val keywords = listOf(
      PACKAGE, FUN, RETURN, IMPORT, VAL, THIS, IS, WHEN, ELSE, PRIVATE, CONST, AS, IF, IN,
      SEALED, CLASS, ABSTRACT, DATA, OVERRIDE, NullLiteral, OBJECT, GETTER
    )
    return tokenType in keywords
  }

  private fun baseHtmlFileName(fileName: String, commitHash: String): String {
    return "$fileName-${shortCommitHash(commitHash)}$EXTENSION_HTML"
  }

  private fun shortCommitHash(commitHash: String): String {
    val range = 0..((commitHash.length - 1).coerceAtMost(COMMIT_HASH_RANGE))
    return commitHash.substring(range)
  }

  private fun debug(message: String) {
    if (!debug) return
    println("[D] $message")
  }

  private fun ShellCommand.log() {
    debug("Executing `$this`")
  }
}
