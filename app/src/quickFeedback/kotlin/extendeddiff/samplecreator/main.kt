@file:Suppress("SameParameterValue")
package extendeddiff.samplecreator

import java.io.File

private const val NEWLINE = "\n"
private const val QUOTE = "\""
private const val EMPTY_STRING = ""

fun main() {
  val outputDirectory = File("/Users/ragunathjawahar/Desktop/extended-diff-demo")
  val fileToInspect = """app/src/main/kotlin/io/redgreen/timelapse/extendeddiff/ExtendedDiffHtml.kt"""

  if (!outputDirectory.exists()) {
    outputDirectory.mkdirs()
  }

  val commitsAffectingFile = commitsAffectingFile(fileToInspect)

  val seedCommitId = commitsAffectingFile.first()
  val seedText = getSeedText(seedCommitId, fileToInspect)
  println(seedText)
}

private fun commitsAffectingFile(filePath: String): List<String> {
  val commitsAffectingFileCommand = """git log --pretty=format:"%h" --follow -- $filePath"""
  return getCommandOutput(commitsAffectingFileCommand)
    .split(NEWLINE)
    .map { it.replace(QUOTE, EMPTY_STRING) }
    .reversed()
}

private fun getSeedText(seedCommitId: String, fileToInspect: String): String {
  val showFileContentsCommand = """git show $seedCommitId:$fileToInspect"""
  return getCommandOutput(showFileContentsCommand)
}

private fun getCommandOutput(command: String): String {
  return Runtime
    .getRuntime()
    .exec(command)
    .inputStream
    .bufferedReader()
    .readText()
}
