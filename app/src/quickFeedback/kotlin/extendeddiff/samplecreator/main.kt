@file:Suppress("SameParameterValue")

package extendeddiff.samplecreator

import java.io.File

private const val NEWLINE = "\n"
private const val QUOTE = "\""
private const val EMPTY_STRING = ""

private const val repositoryPath = "/Users/ragunathjawahar/GitHubProjects/simple-android/"
private const val filePath = "app/src/main/java/org/simple/clinic/search/PatientSearchScreen.kt"
private val outputDirectory = File("/Users/ragunathjawahar/Desktop/simple-android-demo")

fun main() {
  val seedTextFile = outputDirectory.resolve("seed.txt")

  if (!outputDirectory.exists()) {
    outputDirectory.mkdirs()
  }

  val commitsAffectingFile = commitsAffectingFile(repositoryPath, filePath)

  val seedCommitId = commitsAffectingFile.first()
  writeSeedFile(seedCommitId, filePath, seedTextFile)
  writePatchFiles(commitsAffectingFile, seedCommitId, filePath, outputDirectory)
}

private fun writeSeedFile(
  seedCommitId: String,
  filePath: String,
  seedTextOutputFile: File
) {
  val seedText = getSeedText(repositoryPath, seedCommitId, filePath)
  writeToFile(seedText, seedTextOutputFile)
}

private fun writePatchFiles(
  commitsAffectingFile: List<String>,
  seedCommitId: String,
  filePath: String,
  outputDirectory: File
) {
  commitsAffectingFile.drop(1).foldIndexed(seedCommitId) { index, ancestor, descendent ->
    val diff = diff(repositoryPath, filePath, ancestor, descendent)
    writeToFile(diff, outputDirectory.resolve(getPatchFileName(index)))
    descendent
  }
}

private fun getPatchFileName(index: Int): String =
  "${String.format("%02d", index + 1)}.patch"

private fun commitsAffectingFile(repositoryPath: String, filePath: String): List<String> {
  val commitsAffectingFileCommand = """git --git-dir $repositoryPath.git log --pretty=format:"%h" --follow -- $filePath"""
  return getCommandOutput(commitsAffectingFileCommand)
    .split(NEWLINE)
    .map { it.replace(QUOTE, EMPTY_STRING) }
    .reversed()
}

private fun getSeedText(repositoryPath: String, seedCommitId: String, fileToInspect: String): String {
  val showFileContentsCommand = """git --git-dir $repositoryPath.git show $seedCommitId:$fileToInspect"""
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

private fun diff(repositoryPath: String, fileToInspect: String, ancestor: String, descendent: String): String {
  val gitDiffCommand = """git --git-dir $repositoryPath.git diff -u $ancestor $descendent -- $fileToInspect"""
  return getCommandOutput(gitDiffCommand)
}
