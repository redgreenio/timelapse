package io.redgreen.timelapse.domain

import java.util.regex.Pattern

private const val INSERTIONS_REGEX = "\\d+ insertions?\\(\\+\\)"
private const val DELETIONS_REGEX = "\\d+ deletions?\\(-\\)"

private typealias Insertions = Int
private typealias Deletions = Int

fun parseGitFollowOutput(output: String): List<Change> {
  val lines = output.split("\n")

  val commitIdMessageLines = lines.filterIndexed { index, _ -> index == 0 || index % 3 == 0 }
  val insertionDeletionLines = lines.filterIndexed { index, _ -> (index + 1) % 3 == 0 }

  return commitIdMessageLines.zip(insertionDeletionLines) { commitIdMessageLine, insertionsDeletionsLine ->
    val (commitId, message) = commitIdMessageLine.splitIntoTwo(' ')
    val (insertions, deletions) = insertionsDeletionsLine.getInsertionsAndDeletions()
    Change(commitId, message, insertions, deletions)
  }
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

private fun String.getInsertionsAndDeletions(): Pair<Insertions, Deletions> {
  val insertions = this.extractIntFromRegex(INSERTIONS_REGEX)
  val deletions = this.extractIntFromRegex(DELETIONS_REGEX)
  return insertions to deletions
}
