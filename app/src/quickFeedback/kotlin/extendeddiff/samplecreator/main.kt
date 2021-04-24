@file:Suppress("SameParameterValue")

package extendeddiff.samplecreator

import java.io.File

private const val NEWLINE = "\n"
private const val QUOTE = "\""
private const val EMPTY_STRING = ""

fun main() {
  val filePath = """app/src/main/kotlin/io/redgreen/timelapse/extendeddiff/ExtendedDiffHtml.kt"""
  val outputDirectory = File("/Users/ragunathjawahar/Desktop/extended-diff-demo")
  val seedTextFile = outputDirectory.resolve("seed.txt")

  if (!outputDirectory.exists()) {
    outputDirectory.mkdirs()
  }

  val commitsAffectingFile = commitsAffectingFile(filePath)

  val seedCommitId = commitsAffectingFile.first()
  writeSeedFile(seedCommitId, filePath, seedTextFile)
  writePatchFiles(commitsAffectingFile, seedCommitId, filePath, outputDirectory)
}

private fun writeSeedFile(
  seedCommitId: String,
  filePath: String,
  seedTextOutputFile: File
) {
  val seedText = getSeedText(seedCommitId, filePath)
  writeToFile(seedText, seedTextOutputFile)
}

private fun writePatchFiles(
  commitsAffectingFile: List<String>,
  seedCommitId: String,
  filePath: String,
  outputDirectory: File
) {
  commitsAffectingFile.drop(1).foldIndexed(seedCommitId) { index, ancestor, descendent ->
    val diff = diff(ancestor, descendent, filePath)
    writeToFile(diff, outputDirectory.resolve(getPatchFileName(index)))
    descendent
  }
}

private fun getPatchFileName(index: Int): String =
  "${String.format("%02d", index + 1)}.patch"

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

private fun writeToFile(content: String, outputFile: File) {
  outputFile.writeText(content)
}

private fun diff(ancestor: String, descendent: String, fileToInspect: String): String {
  val gitDiffCommand = """git diff -u $ancestor $descendent -- $fileToInspect"""
  return getCommandOutput(gitDiffCommand)
}
