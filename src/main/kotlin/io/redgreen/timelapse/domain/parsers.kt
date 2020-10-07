package io.redgreen.timelapse.domain

import java.util.regex.Pattern

fun parseGitFollowOutput(output: String): Change {
  val lines = output.split("\n")
  val commitIdMessageLine = lines[0]
  val commitAndMessageSeparatorIndex = commitIdMessageLine.indexOf(' ')
  val commitId = commitIdMessageLine.substring(0, commitAndMessageSeparatorIndex)
  val message = commitIdMessageLine.substring(commitAndMessageSeparatorIndex + 1)

  val insertionsDeletionsLine = lines[2]
  val pattern = Pattern.compile("\\d+ insertions\\(\\+\\)")
  val matcher = pattern.matcher(insertionsDeletionsLine)
  val insertions = if (matcher.find()) {
    matcher.group().split(" ")[0].toInt()
  } else {
    throw IllegalStateException("Unexpected input: $insertionsDeletionsLine")
  }

  return Change(commitId, message, insertions)
}
