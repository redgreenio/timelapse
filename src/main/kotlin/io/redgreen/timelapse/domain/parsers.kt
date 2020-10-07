package io.redgreen.timelapse.domain

import java.util.regex.Pattern

fun parseGitFollowOutput(output: String): Change {
  val lines = output.split("\n")
  val commitIdMessageLine = lines[0]
  val commitAndMessageSeparatorIndex = commitIdMessageLine.indexOf(' ')
  val commitId = commitIdMessageLine.substring(0, commitAndMessageSeparatorIndex)
  val message = commitIdMessageLine.substring(commitAndMessageSeparatorIndex + 1)

  val insertionsDeletionsLine = lines[2]
  val insertionsPattern = Pattern.compile("\\d+ insertions\\(\\+\\)")
  val insertionsMatcher = insertionsPattern.matcher(insertionsDeletionsLine)
  val insertions = if (insertionsMatcher.find()) {
    insertionsMatcher.group().split(" ")[0].toInt()
  } else {
    0
  }

  val deletionsPattern = Pattern.compile("\\d+ deletions\\(-\\)")
  val deletionsMatcher = deletionsPattern.matcher(insertionsDeletionsLine)
  val deletions = if (deletionsMatcher.find()) {
    deletionsMatcher.group().split(" ")[0].toInt()
  } else {
    0
  }

  return Change(commitId, message, insertions, deletions)
}
