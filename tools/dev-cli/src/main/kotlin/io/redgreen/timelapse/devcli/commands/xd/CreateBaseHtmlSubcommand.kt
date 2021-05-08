package io.redgreen.timelapse.devcli.commands.xd

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

    val filePath = getFilePath(fileName)
    val fileContent = getFileContent(commitHash, filePath)

    println(fileContent)
  }

  // git ls-files ExtendedDiffHtml.kt '**/ExtendedDiffHtml.kt'
  private fun getFilePath(fileName: String): String {
    val gitFindFilePathProcess = ProcessBuilder()
      .command("git", "ls-files", fileName, "**/$fileName")
      .start()
    return gitFindFilePathProcess.inputStream.reader().readText().trim()
  }

  // git show 1f69e11d..66273236:app/src/main/kotlin/io/redgreen/timelapse/extendeddiff/ExtendedDiffHtml.kt
  private fun getFileContent(commitHash: String, filePath: String): String {
    val gitFileContentProcess = ProcessBuilder()
      .command("git", "show", "$commitHash:$filePath")
      .start()

    val readText = gitFileContentProcess.errorStream.reader().readText()
    println(readText)

    return gitFileContentProcess.inputStream.reader().readText()
  }
}
