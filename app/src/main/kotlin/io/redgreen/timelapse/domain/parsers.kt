package io.redgreen.timelapse.domain

import io.redgreen.timelapse.domain.Change.Modification
import io.redgreen.timelapse.domain.Change.Move
import java.util.regex.Pattern

private const val INSERTIONS_REGEX = "\\d+ insertions?\\(\\+\\)"
private const val DELETIONS_REGEX = "\\d+ deletions?\\(-\\)"
private const val GIT_HEAD_REGEX = "\\(.+ -> .+\\)"
private const val FILE_MOVE_REGEX = "\\{.+ => .+}"
private const val FILE_MOVE_SEPARATOR = " => "

private typealias Insertions = Int
private typealias Deletions = Int
private typealias CommitIdMessageLine = String
private typealias InsertionsDeletionsLine = String

fun parseGitFollowOutput(output: String): List<Change> {
  val lines = output.split("\n")

  val commitIdMessageLines = lines.filterIndexed { index, _ -> index == 0 || index % 3 == 0 }
  val insertionDeletionLines = lines.filterIndexed { index, _ -> (index + 1) % 3 == 0 }
  val fileNameLines = lines.filterIndexed { index, _ -> index != 0 && (index - 1) % 3 == 0 }

  return commitIdMessageLines.zip(insertionDeletionLines).zip(fileNameLines, ::extractCommitInformation)
}

private fun extractCommitInformation(
  pairs: Pair<CommitIdMessageLine, InsertionsDeletionsLine>,
  fileChangeLine: String
): Change {
  val (commitIdMessageLine, insertionsDeletionsLine) = pairs
  val (commitId, possiblyMessageWithBranchInformation) = commitIdMessageLine.splitIntoTwo(' ')
  val (insertions, deletions) = insertionsDeletionsLine.getInsertionsAndDeletions()
  val message = extractMessage(possiblyMessageWithBranchInformation)

  val matcher = Pattern.compile(FILE_MOVE_REGEX).matcher(fileChangeLine)
  return if (!matcher.find()) {
    Modification(commitId, message, insertions, deletions)
  } else {
    val moveInformationFragment = matcher.group()
    val (oldFragment, newFragment) = moveInformationFragment
      .substring(1, moveInformationFragment.length - 1)
      .splitIntoTwo(FILE_MOVE_SEPARATOR)
    val (dirtyOldPathFragment) = fileChangeLine.replace(moveInformationFragment, oldFragment).splitIntoTwo('|')
    val (dirtyNewPathFragment) = fileChangeLine.replace(moveInformationFragment, newFragment).splitIntoTwo('|')

    val oldPathFragment = dirtyOldPathFragment.trim().replace(".../", "/")
    val newPathFragment = dirtyNewPathFragment.trim().replace(".../", "/")
    Move(commitId, message, insertions, deletions, oldPathFragment, newPathFragment)
  }
}

private fun String.splitIntoTwo(delimiter: Char): Pair<String, String> =
  splitIntoTwo(delimiter.toString())

private fun String.splitIntoTwo(delimiter: String): Pair<String, String> {
  val delimiterIndex = this.indexOf(delimiter)
  val first = this.substring(0, delimiterIndex)
  val second = this.substring(delimiterIndex + delimiter.length)
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

private fun extractMessage(possiblyMessageWithBranchInformation: String): String {
  val pattern = Pattern.compile(GIT_HEAD_REGEX)
  val matcher = pattern.matcher(possiblyMessageWithBranchInformation)
  return if (matcher.find()) {
    possiblyMessageWithBranchInformation.substring(matcher.group().length).trim()
  } else {
    possiblyMessageWithBranchInformation
  }
}
