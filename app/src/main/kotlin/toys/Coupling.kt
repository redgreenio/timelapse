@file:Suppress("MagicNumber")

package toys

import com.github.tomaslanger.chalk.Chalk
import com.jakewharton.picnic.table
import java.util.Locale
import toys.Relationship.DifferentModule
import toys.Relationship.SameModule
import toys.Relationship.TestModule

typealias FilePath = String
typealias Count = Int

fun main() {
  // - What are the most important files in this project?
  // - How are they related to each other?

  // - Who are you working with?
  // - What are the most important files in your module?
  // - How are these files related to each other?

  // - Put a fun spin on, get the bigger picture of your product?

  // - God classes in the project (probably requires refactoring, large classes is a proxy for refactoring)

  // Very large projects: Only query history for a certain module.

  // Is this file located in the right place? Where should this file be located?

  val gitDirectory = "/Users/ragunathjawahar/IdeaProjects/timelapse/.git"
  val filePath = "app/src/main/kotlin/io/redgreen/timelapse/openrepo/GitDetector.kt"
  val filterSingleChangeFiles = false

  val commitSHAs = extractCommitIds(getFileCommitHistory(gitDirectory, filePath))

  val registry = mutableMapOf<FilePath, Count>()
  val totalCommits = commitSHAs.size

  commitSHAs
    .map { getChangedFiles(gitDirectory, filePath, it) }
    .onEach { changedFilesInCommit ->
      changedFilesInCommit.onEach { changedFile ->
        val changeCount = if (registry.containsKey(changedFile)) {
          registry[changedFile]!! + 1
        } else {
          1
        }
        registry[changedFile] = changeCount
      }
    }

  val filesInCurrentRevision = getFilesInCurrentRevision(gitDirectory)

  val allChangedFiles = registry.keys
  val couplingRows = allChangedFiles
    .map { CouplingRow(it, registry[it]!!) }
    .filter { registry[it.filePath]!! > if (filterSingleChangeFiles) 1 else 0 }
    .filter { filesInCurrentRevision.contains(it.filePath) }
    .toMutableList()
  couplingRows.sortDescending() // .sortByDescending { it.changeCount }

  val table = table {
    header {
      cellStyle {
        border = true
      }
      val fileBeingInspectedHeader = " File: $filePath [${couplingRows.size}]"
      val changeCountHeader = " Change count "
      val degreeHeader = " Degree "

      row(Chalk.on(fileBeingInspectedHeader).bold(), Chalk.on(changeCountHeader).bold(), Chalk.on(degreeHeader).bold())
    }

    cellStyle {
      border = true
    }
    couplingRows
      .onEach { row ->
        val count = row.changeCount
        val couplingDegreePercent = count.toDouble() / totalCommits * 100

        val relationship = guessRelationship(filePath, row.filePath)
        val filePathValue = relationship.colorByRelationship(row.filePath)
        val countByTotalCommits = relationship.colorByRelationship(" $count/$totalCommits ")
        val couplingPercentText = relationship.colorByRelationship(formatToPercent(couplingDegreePercent))

        row(filePathValue, countByTotalCommits, couplingPercentText)
      }
  }
  println(table)
}

private fun formatToPercent(couplingDegreePercent: Double) =
  String.format(Locale.ENGLISH, " %.02f%% ", couplingDegreePercent)

private fun Relationship.colorByRelationship(
  text: String
): Chalk = when (this) {
  SameModule -> Chalk.on(" $text ").yellow()
  TestModule -> Chalk.on(" $text ").green()
  DifferentModule -> Chalk.on(" $text ").red().bold()
}

fun guessRelationship(
  inspectedFilePath: String,
  changedFilePath: String
): Relationship {
  val inspectedFilePathDirectory = inspectedFilePath.substring(0, inspectedFilePath.lastIndexOf('/') + 1)
  val changedFilePathDirectory = changedFilePath.substring(0, changedFilePath.lastIndexOf('/') + 1)

  return when {
    isSameModule(changedFilePath, inspectedFilePathDirectory, changedFilePathDirectory) -> SameModule
    isTestModule(inspectedFilePathDirectory, changedFilePath) -> TestModule
    else -> DifferentModule
  }
}

private fun isSameModule(
  changedFilePath: String,
  inspectedFilePathDirectory: String,
  changedFilePathDirectory: String
): Boolean {
  return changedFilePath.startsWith(inspectedFilePathDirectory) ||
    (changedFilePathDirectory.isNotBlank() && inspectedFilePathDirectory.endsWith(changedFilePathDirectory))
}

@Suppress("SameParameterValue")
private fun getFileCommitHistory(
  gitDirectory: String,
  filePath: String
): String {
  return Runtime
    .getRuntime()
    .exec(arrayOf("git", "--git-dir", gitDirectory, "log", "--pretty=oneline", "--follow", "--", filePath))
    .inputStream
    .reader()
    .use { it.readText() }
}

@Suppress("SameParameterValue")
private fun getChangedFiles(
  gitDirectory: String,
  filePath: String,
  commitSha: String
): List<String> {
  val rawDiffOutput = Runtime
    .getRuntime()
    // TODO Handle first commit in repo
    .exec(arrayOf("git", "--git-dir", gitDirectory, "diff", "--name-status", commitSha, "$commitSha^1"))
    .inputStream
    .reader()
    .use { it.readText() }

  return rawDiffOutput
    .split('\n')
    .filter { it.isNotBlank() }
    .map { it.split('	').last() }
    .filter { it != filePath && !filePath.endsWith(it) }
}

private fun extractCommitIds(historyLog: String): List<String> {
  return historyLog
    .lines()
    .map { it.split(' ').first() }
    .filter { it.isNotBlank() }
}

private fun isTestModule(
  inspectedFilePathDirectory: String,
  changedFilePath: String
): Boolean {
  return changedFilePath
    .replace("test", "main")
    .startsWith(inspectedFilePathDirectory)
}

data class CouplingRow(
  val filePath: String,
  val changeCount: Int
) : Comparable<CouplingRow> {
  override fun compareTo(other: CouplingRow): Int {
    val changeCountComparison = changeCount - other.changeCount
    if (changeCountComparison != 0) return changeCountComparison
    return other.filePath.compareTo(filePath)
  }
}

enum class Relationship {
  SameModule, TestModule, DifferentModule
}
