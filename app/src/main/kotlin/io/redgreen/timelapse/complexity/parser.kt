package io.redgreen.timelapse.complexity

import io.redgreen.timelapse.git.model.CommitHash
import io.redgreen.timelapse.git.model.Identity
import java.time.ZonedDateTime

private const val DELIMITER = " |@| "

/**
 * Parser for command,
 * git log --pretty='%H |@| %aN |@| %ae |@| %aI%n%cN |@| %ce |@| %cI%n%s' --numstat --follow -- <file-path>
 */
fun parse(rawCommit: String): ParsedCommit {
  val lines = rawCommit.lines()
  val (sha, authorName, authorEmail, authoredIso) = lines[0].split(DELIMITER)
  val (committerName, committerEmail, committedIso) = lines[1].split(DELIMITER)
  val summary = lines[2]
  val (insertions, deletions, filePath) = lines.last().trim().replace(Regex(" +"), " ").split(" ")

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
