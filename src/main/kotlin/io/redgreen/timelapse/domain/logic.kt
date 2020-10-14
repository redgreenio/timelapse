package io.redgreen.timelapse.domain

import java.io.File

fun getCommitHistoryText(
  projectDirectory: String,
  filePath: String
): String {
  val projectPath = File(projectDirectory).absolutePath
  val gitDirectory = "$projectPath${File.separator}.git"

  val process = Runtime
    .getRuntime()
    .exec(gitLogCommand(gitDirectory, filePath))

  return process.inputStream.reader().use { it.readText() }
}

private fun gitLogCommand(gitDirectory: String, filePath: String): Array<String> {
  return arrayOf(
    "git",
    "--git-dir",
    gitDirectory,
    "log",
    "--oneline",
    "-M",
    "--stat",
    "--no-merges",
    /* "--follow",*/
    "--",
    filePath,
  )
}
