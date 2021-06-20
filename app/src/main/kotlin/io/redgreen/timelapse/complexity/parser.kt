package io.redgreen.timelapse.complexity

import io.redgreen.timelapse.git.model.CommitHash
import io.redgreen.timelapse.git.model.Identity
import java.time.ZonedDateTime

private const val DELIMITER = " |@| "
private const val RAW_COMMIT_LINE_COUNT = 5
private const val NEWLINE = "\n"
private const val TAB = "\t"
private const val SPACE = " "

/**
 * Parser for command,
 * git log --pretty='%H |@| %aN |@| %ae |@| %aI%n%cN |@| %ce |@| %cI%n%s' --numstat --follow -- <file-path>
 */
internal fun parse(rawCommit: String): ParsedCommit {
  val lines = rawCommit.lines()
  val (sha, authorName, authorEmail, authoredIso) = lines[0].split(DELIMITER)
  val (committerName, committerEmail, committedIso) = lines[1].split(DELIMITER)
  val summary = lines[2]
  val (insertions, deletions, filePath) = sanitizeStatsLine(lines.last()).split(" ")

  return ParsedCommit(
    CommitHash(sha),
    summary,
    Identity(authorName, authorEmail),
    ZonedDateTime.parse(authoredIso),
    Identity(committerName, committerEmail),
    ZonedDateTime.parse(committedIso),
    filePath,
    Stats(insertions.toInt(), deletions.toInt())
  )
}

fun parseAll(rawCommits: String): List<ParsedCommit> {
  val allLines = rawCommits.lines()
  val splitRawCommits = mutableListOf<String>()
  for (i in 0..allLines.lastIndex step RAW_COMMIT_LINE_COUNT) {
    splitRawCommits.add(allLines.subList(i, i + RAW_COMMIT_LINE_COUNT).joinToString(NEWLINE))
  }
  return splitRawCommits.map(::parse)
}

private fun sanitizeStatsLine(statsLine: String): String {
  return statsLine.trim()
    .replace(Regex(" +"), SPACE) // Multiple spaces with a single space
    .replaceFirst(TAB, SPACE) // First tab character with a space
    .replaceFirst(TAB, SPACE) // Second tab character with a space
}
