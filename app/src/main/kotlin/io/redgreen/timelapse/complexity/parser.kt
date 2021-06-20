package io.redgreen.timelapse.complexity

import io.redgreen.timelapse.git.model.CommitHash
import io.redgreen.timelapse.git.model.Identity
import java.time.ZonedDateTime

private const val DELIMITER = " |@| "
private const val RAW_COMMIT_LINE_COUNT = 5
private const val NEWLINE = "\n"
private const val TAB = "\t"
private const val SPACE = " "
private const val OPEN_CURLY = "{"
private const val ARROW = " => "
private const val CLOSE_CURLY = "}"

fun parseAll(rawCommits: String): List<ParsedCommit> {
  val allLines = rawCommits.lines()
  val splitRawCommits = mutableListOf<String>()
  for (i in 0..allLines.lastIndex step RAW_COMMIT_LINE_COUNT) {
    splitRawCommits.add(allLines.subList(i, i + RAW_COMMIT_LINE_COUNT).joinToString(NEWLINE))
  }
  return splitRawCommits.map(::parse)
}

/**
 * Parser for command,
 * git log --pretty='%H |@| %aN |@| %ae |@| %aI%n%cN |@| %ce |@| %cI%n%s' --numstat --follow -- <file-path>
 */
internal fun parse(rawCommit: String): ParsedCommit {
  val lines = rawCommit.lines()
  val (sha, authorName, authorEmail, authoredIso) = lines[0].split(DELIMITER)
  val (committerName, committerEmail, committedIso) = lines[1].split(DELIMITER)
  val summary = lines[2]
  val (insertions, deletions, filePath) = statsAndFilePath(lines.last())

  return ParsedCommit(
    CommitHash(sha),
    summary,
    Identity(authorName, authorEmail),
    ZonedDateTime.parse(authoredIso),
    Identity(committerName, committerEmail),
    ZonedDateTime.parse(committedIso),
    filePath,
    Stats(insertions, deletions)
  )
}

private fun statsAndFilePath(statsLine: String): Triple<Int, Int, String> {
  val sanitizedStatsLine = sanitizeStatsLine(statsLine)
  val firstSpaceCharIndex = sanitizedStatsLine.indexOf(SPACE)
  val secondSpaceCharIndex = sanitizedStatsLine.indexOf(SPACE, firstSpaceCharIndex + 1)

  val insertions = sanitizedStatsLine.substring(0, firstSpaceCharIndex).toInt()
  val deletions = sanitizedStatsLine.substring(firstSpaceCharIndex + 1, secondSpaceCharIndex).toInt()
  val filePath = getFilePath(sanitizedStatsLine, secondSpaceCharIndex)

  return Triple(insertions, deletions, filePath)
}

private fun sanitizeStatsLine(statsLine: String): String {
  return statsLine.trim()
    .replace(Regex(" +"), SPACE) // Multiple spaces with a single space
    .replaceFirst(TAB, SPACE) // First tab character with a space
    .replaceFirst(TAB, SPACE) // Second tab character with a space
}

private fun getFilePath(sanitizedStatsLine: String, filePathBeginIndex: Int): String {
  val filePathInformation = sanitizedStatsLine.substring(filePathBeginIndex + 1)

  val openCurlyIndex = filePathInformation.indexOf(OPEN_CURLY)
  val arrowIndex = filePathInformation.indexOf(ARROW)
  val closeCurlyIndex = filePathInformation.indexOf(CLOSE_CURLY)

  val renamedFile = arrowIndex in (openCurlyIndex + 1) until closeCurlyIndex

  return if (renamedFile) {
    val (_, newSegment) = filePathInformation.substring(openCurlyIndex, closeCurlyIndex).split(ARROW)

    filePathInformation.substring(0, openCurlyIndex) +
      newSegment +
      filePathInformation.substring(closeCurlyIndex + 1)
  } else {
    filePathInformation
  }
}
