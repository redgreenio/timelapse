package io.redgreen.timelapse.domain

import java.util.regex.Pattern

private const val INSERTIONS_REGEX = "\\d+ insertions?\\(\\+\\)"
private const val DELETIONS_REGEX = "\\d+ deletions?\\(-\\)"

fun parseGitFollowOutput(output: String): Change {
  val lines = output.split("\n")

  val commitIdMessageLine = lines[0]
  val insertionsDeletionsLine = lines[2]

  val (commitId, message) = commitIdMessageLine.splitIntoTwo(' ')
  val insertions = insertionsDeletionsLine.extractIntFromRegex(INSERTIONS_REGEX)
  val deletions = insertionsDeletionsLine.extractIntFromRegex(DELETIONS_REGEX)

  return Change(commitId, message, insertions, deletions)
}

private fun String.splitIntoTwo(delimiter: Char): Pair<String, String> {
  val delimiterIndex = this.indexOf(delimiter)
  val first = this.substring(0, delimiterIndex)
  val second = this.substring(delimiterIndex + 1)
  return first to second
}

private fun String.extractIntFromRegex(regex: String): Int {
  val pattern = Pattern.compile(regex)
  val matcher = pattern.matcher(this)
  return if (matcher.find()) {
    matcher.group().splitIntoTwo(' ').first.toInt()
  } else {
    0
  }
}
