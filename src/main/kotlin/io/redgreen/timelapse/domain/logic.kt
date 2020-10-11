package io.redgreen.timelapse.domain

import java.io.File

fun getCommitHistoryText(
  projectDirectory: String,
  filePath: String
): String {
  val projectPath = "${File(projectDirectory).absolutePath}${File.separator}"
  val gitDirectory = "$projectPath.git"

  val command = arrayOf("git", "--git-dir", gitDirectory, "log", "--oneline", "-M", "--stat", "--follow", "--", filePath)
  val process = Runtime
    .getRuntime()
    .exec(command)

  return process.inputStream.reader().use { it.readText() }
}
