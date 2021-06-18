package toys

import io.redgreen.timelapse.do_not_rename.UserSettingsNode
import kotlin.system.measureTimeMillis

fun main() {
  val timeInMillis = measureTimeMillis {
    printLocAcrossCommits()
  }
  println("Took ${timeInMillis}ms.")
}

private fun printLocAcrossCommits() {
  val churnFileFromResources = "ExtendedDiffHtml-churn.txt"
  val rawText = UserSettingsNode::class.java.getResourceAsStream("/toys/$churnFileFromResources")!!
    .reader()
    .readText()

  rawText
    .lines()
    .filter { it.contains("1 file changed") }
    .reversed()
    .scan(0) { loc, line ->
      val insertions = getInsertions(line)
      val deletions = getDeletions(line)
      loc + insertions - deletions
    }
    .onEach { println(it) }
}

private fun getInsertions(line: String): Int =
  getNumber(line, "insertion")

private fun getDeletions(line: String): Int =
  getNumber(line, "deletion")

private fun getNumber(line: String, term: String): Int {
  val splits = line.split(' ')
  val deletionText = splits.find { it.contains(term) } ?: return 0
  val index = splits.indexOf(deletionText)
  return if (index != -1) {
    splits[index - 1].toInt()
  } else {
    0
  }
}
