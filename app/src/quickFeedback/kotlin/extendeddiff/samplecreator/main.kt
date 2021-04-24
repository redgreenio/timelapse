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

  commitsAffectingFile.onEach { println(it) }
}

private fun commitsAffectingFile(
  @Suppress("SameParameterValue") filePath: String
): List<String> {
  val commitsAffectingFileCommand = """git log --pretty=format:"%h" --follow -- $filePath"""
  return Runtime
    .getRuntime()
    .exec(commitsAffectingFileCommand)
    .inputStream
    .bufferedReader()
    .readText()
    .split(NEWLINE)
    .map { it.replace(QUOTE, EMPTY_STRING) }
    .reversed()
}
