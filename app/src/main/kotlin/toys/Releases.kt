@file:Suppress("MagicNumber", "MaxLineLength", "LongMethod")

package toys

import com.github.tomaslanger.chalk.Chalk
import java.io.File
import java.lang.Integer.parseInt
import java.util.Locale
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode.REMOTE
import toys.PercentChange.Decrease
import toys.PercentChange.Increase
import toys.PercentChange.NoChange

abstract class Configuration(
  val gitDirectory: String,
  val refPrefix: String,
  val firstCommitId: String
) {
  abstract fun namesCommits(): List<Pair<String, String>>
}

@Suppress("unused")
private val timelapseConfiguration = object : Configuration(
  "/Users/ragunathjawahar/IdeaProjects/timelapse/.git",
  "refs/tags/",
  "d38c3320c6caa7da1b19c8648d889f48ce22a798"
) {
  override fun namesCommits(): List<Pair<String, String>> {
    val repository = Git.open(File(gitDirectory))
    val refs = repository.tagList().call()
    val names = refs.map { it.name }
    val commits = refs.map { it.objectId.name }
    return names.zip(commits)
  }
}

@Suppress("unused")
private val simpleAndroidConfiguration = object : Configuration(
  "/Users/ragunathjawahar/GitHubProjects/simple-android/.git",
  "refs/remotes/origin/release/",
  "a323911033b59aa5bdb57e55860f9d9fad36cea8"
) {
  override fun namesCommits(): List<Pair<String, String>> {
    val repository = Git.open(File(gitDirectory))
    val releaseBranches = repository
      .branchList()
      .setListMode(REMOTE)
      .call()
      .filter { it.name.contains("origin/release/") }

    val names = releaseBranches.map { it.name }
    val commits = releaseBranches.map { it.objectId.name }
    return names.zip(commits)
  }
}

@Suppress("unused")
private val angularConfiguration = object : Configuration(
  "/Users/ragunathjawahar/GitHubProjects/angular/.git",
  "refs/tags/",
  "6a3abf2366e2c32ce3460155903262fee01736c8"
) {
  override fun namesCommits(): List<Pair<String, String>> {
    val repository = Git.open(File(gitDirectory))
    val refs = repository.tagList().call()
    val names = refs.map { it.name }
    val commits = refs.map { it.objectId.name }
    return names.zip(commits)
  }
}

private val currentConfiguration = simpleAndroidConfiguration

fun main() {
  val releaseCommits = currentConfiguration.namesCommits()
  val firstCommitPlusReleases = releaseCommits.dropLast(1).toMutableList().apply {
    add(0, Pair("~inception~", currentConfiguration.firstCommitId))
  }

  val releasePairs = firstCommitPlusReleases.zip(releaseCommits)

  val releaseDiffSummaries = releasePairs
    .map { (branchA, branchB) ->
      getDiffStat(
        currentConfiguration.gitDirectory,
        branchA.second,
        branchB.second
      ) to branchB
    }
    .map { (diff, branch) -> diff.lines().takeLast(2).dropLast(1).first() to branch }
    .map { (summary, branch) -> displayName(branch.first) to parseSummary(summary) }

  val releaseDiffSummariesWithoutFirst = releaseDiffSummaries.drop(1)

  val boundary =
    "============================================================================================================="
  println(boundary)
  println("${Chalk.on("Releases").bold()}                 ║ ${Chalk.on("Files Changed").bold()} | ${Chalk.on("%").bold()}           ║ ${Chalk.on("Insertions").bold()} | ${Chalk.on("%").bold()}            ║ ${Chalk.on("Deletions").bold()} | ${Chalk.on("%").bold()}")
  println(boundary)
  val firstReleaseDiffStat = parseSummary(
    getDiffStat(
      currentConfiguration.gitDirectory,
      currentConfiguration.firstCommitId,
      releaseCommits.first().second
    ).lines().takeLast(2).dropLast(1).first()
  )
  println(" (initial) -> ${displayName(releaseCommits.first().first)} ║ ${firstReleaseDiffStat.filesChanged.toString().padStart(13)} | ${"-".padStart(11)} ║ ${firstReleaseDiffStat.insertions.toString().padStart(10)} | ${"-".padStart(12)} ║ ${firstReleaseDiffStat.deletions.toString().padStart(9)} | ${"-".padStart(12)}")

  releaseDiffSummaries.zip(releaseDiffSummariesWithoutFirst) { (previousBranch, previousDiffSummary), (currentBranch, currentDiffSummary) ->
    val (filesChangedPercentChange, filesChangedPercentText) =
      percentText(currentDiffSummary.filesChanged, previousDiffSummary.filesChanged)

    val (insertionsPercentChange, insertionsPercentText) =
      percentText(currentDiffSummary.insertions, previousDiffSummary.insertions)

    val (deletionsPercentChange, deletionsPercentText) =
      percentText(currentDiffSummary.deletions, previousDiffSummary.deletions)

    val versionChange = "$previousBranch -> $currentBranch"

    val coloredDeletionsPercentText = when (deletionsPercentChange) {
      is Decrease -> Chalk.on(deletionsPercentText.padStart(12)).red()
      is Increase -> Chalk.on(deletionsPercentText.padStart(12)).green()
      NoChange -> Chalk.on(deletionsPercentText.padStart(12)).gray()
    }

    val coloredInsertionsPercentText = when (insertionsPercentChange) {
      is Decrease -> Chalk.on(insertionsPercentText.padStart(12)).red()
      is Increase -> Chalk.on(insertionsPercentText.padStart(12)).green()
      NoChange -> Chalk.on(insertionsPercentText.padStart(12)).gray()
    }

    val coloredFilesChangedPercentText = when (filesChangedPercentChange) {
      is Decrease -> Chalk.on(filesChangedPercentText.padStart(11)).red()
      is Increase -> Chalk.on(filesChangedPercentText.padStart(11)).green()
      NoChange -> Chalk.on(filesChangedPercentText.padStart(11)).red().gray()
    }

    println(
      "$versionChange ║ ${
        currentDiffSummary.filesChanged.toString().padStart(13)
      } | $coloredFilesChangedPercentText ║ ${
        currentDiffSummary.insertions.toString().padStart(10)
      } | $coloredInsertionsPercentText ║ ${
        currentDiffSummary.deletions.toString().padStart(9)
      } | $coloredDeletionsPercentText"
    )
  }
  println(boundary)
}

private fun percentText(
  currentCount: Int,
  previousCount: Int
): Pair<PercentChange, String> {
  val percentChange = PercentChange.calculate(currentCount, previousCount)
  val suffix = if (percentChange is Increase) "▲" else if (percentChange is Decrease) "▼" else ""
  val percentText = String.format(Locale.ENGLISH, "%.02f%% $suffix", percentChange.number)
  return Pair(percentChange, percentText)
}

private fun getDiffStat(gitDirectory: String, commitA: String, commitB: String): String {
  return Runtime
    .getRuntime()
    .exec(arrayOf("git", "--git-dir", gitDirectory, "diff", commitA, commitB, "--stat"))
    .inputStream
    .reader()
    .use { it.readText() }
}

private fun displayName(name: String): String =
  name.substring(currentConfiguration.refPrefix.length)

private fun parseSummary(summary: String): DiffSummary {
  val (filesChangedText, insertionsText, deletionsText) = summary.split(",").map { it.trim() }

  // Breakdown between Kotlin, Java, XMl, etc., and binary too!
  // Get version number from build.gradle
  return DiffSummary(
    parseInt(filesChangedText.substring(0, filesChangedText.indexOf(' '))),
    parseInt(insertionsText.substring(0, insertionsText.indexOf(' '))),
    parseInt(deletionsText.substring(0, deletionsText.indexOf(' '))),
  )
}

data class DiffSummary(
  val filesChanged: Int,
  val insertions: Int,
  val deletions: Int
)

sealed class PercentChange(open val number: Double) {
  companion object {
    fun calculate(newNumber: Int, oldNumber: Int): PercentChange =
      calculate(newNumber.toDouble(), oldNumber.toDouble())

    private fun calculate(newNumber: Double, originalNumber: Double): PercentChange {
      if (newNumber == originalNumber) return NoChange

      val increase = newNumber > originalNumber
      val delta = if (increase) {
        newNumber - originalNumber
      } else {
        originalNumber - newNumber
      }
      val percentageChange = delta / originalNumber * 100

      return if (increase) {
        Increase(percentageChange)
      } else {
        Decrease(percentageChange)
      }
    }
  }

  data class Increase(override val number: Double) : PercentChange(number)
  data class Decrease(override val number: Double) : PercentChange(number)
  object NoChange : PercentChange(0.0)
}
