package io.redgreen.timelapse.domain

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val gitDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

fun getCommitHistoryText(
  projectDirectory: String,
  filePath: String,
  startDateEndDate: Pair<LocalDate, LocalDate>?
): String {
  val projectPath = File(projectDirectory).absolutePath
  val gitDirectory = "$projectPath${File.separator}.git"

  val gitLogCommand = gitLogCommand(gitDirectory, filePath, startDateEndDate)
  val process = Runtime
    .getRuntime()
    .exec(gitLogCommand)

  return process.inputStream.reader().use { it.readText() }
}

private fun gitLogCommand(
  gitDirectory: String,
  filePath: String,
  startDateEndDate: Pair<LocalDate, LocalDate>?
): Array<String> {
  return if (startDateEndDate == null) {
    arrayOf(
      "git", "--git-dir", gitDirectory, "log", "--oneline", "-M", "--stat", "--no-merges", /* "--follow",*/ "--", filePath,
    )
  } else {
    val (startDate, endDate) = startDateEndDate
    val endDateExclusive = endDate.plusDays(1)
    arrayOf(
      "git", "--git-dir", gitDirectory, "log", "--oneline", "-M", "--stat", "--no-merges",
      "--since", startDate.toGitDate(), "--until", endDateExclusive.toGitDate(),/* "--follow",*/ "--", filePath,
    )
  }
}

private fun LocalDate.toGitDate(): String =
  gitDateFormatter.format(this)
