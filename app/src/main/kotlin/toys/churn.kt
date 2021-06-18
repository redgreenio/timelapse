package toys

import io.redgreen.timelapse.do_not_rename.UserSettingsNode
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.treewalk.TreeWalk
import org.jetbrains.kotlin.psi.KtPsiFactory
import toys.metrics.CognitiveComplexityVisitor
import toys.metrics.CyclomaticComplexityVisitor
import toys.metrics.createKtCoreEnvironment
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import kotlin.system.measureTimeMillis

private const val REPO_PATH = "/Users/ragunathjawahar/IdeaProjects/timelapse/.git"
private const val FILE_PATH = "app/src/main/kotlin/io/redgreen/timelapse/extendeddiff/ExtendedDiffHtml.kt"
private const val PADDING = 4

private val REPO = RepositoryBuilder().setGitDir(File(REPO_PATH)).build()

private val CORE_ENVIRONMENT = createKtCoreEnvironment()
private val PSI_FACTORY = KtPsiFactory(CORE_ENVIRONMENT.project, false)

fun main() {
  val timeInMillis = measureTimeMillis {
    val commitHashesAndLoc = getCommitHashesAndLoc()
    commitHashesAndLoc
      .map { commitHashLocPair ->
        val commitHash = commitHashLocPair.first
        val revCommit = REPO.parseCommit(ObjectId.fromString(commitHash))
        val content = getContent(revCommit)!!
        val ktFile = PSI_FACTORY.createFile("FileUnderAnalysis.kt", content)

        val cognitiveComplexityVisitor = CognitiveComplexityVisitor()
        ktFile.accept(cognitiveComplexityVisitor)

        val cyclomaticComplexityVisitor = CyclomaticComplexityVisitor()
        ktFile.accept(cyclomaticComplexityVisitor)

        val metrics = Triple(
          commitHashLocPair.second,
          cognitiveComplexityVisitor.complexity,
          cyclomaticComplexityVisitor.complexity
        )
        commitHash to metrics
      }
      .onEach { (commitHash, metrics) ->
        val (loc, cc, mcc) = metrics
        val paddedLoc = loc.toString().padStart(PADDING)
        val paddedCognitive = cc.toString().padStart(PADDING)
        val paddedCyclomatic = mcc.toString().padStart(PADDING)
        println("$commitHash || $paddedLoc @@ $paddedCognitive ^^ $paddedCyclomatic")
      }
  }
  println("\nTook ${timeInMillis}ms.")
}

@SuppressWarnings("MagicNumber")
private fun getCommitHashesAndLoc(): List<Pair<String, Int>> {
  val churnFileFromResources = "ExtendedDiffHtml-churn.txt"
  val rawText = UserSettingsNode::class.java.getResourceAsStream("/toys/$churnFileFromResources")!!
    .reader()
    .readText()
    .trim()

  val lines = rawText
    .lines()

  val oldestToNewest = lines
    .reversed()

  val locs = oldestToNewest
    .filter { it.contains("1 file changed") }
    .scan(0) { loc, line ->
      val insertions = getInsertions(line)
      val deletions = getDeletions(line)
      loc + insertions - deletions
    }
    .drop(1)

  val commitHashes = oldestToNewest
    .filterIndexed { index, _ -> (index + 1) % 3 == 0 }
    .map { it.split(" ").first() }

  return commitHashes.zip(locs)
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

private fun getContent(commit: RevCommit): String? {
  val git = Git(REPO)
  try {
    TreeWalk.forPath(git.repository, FILE_PATH, commit.tree).use { treeWalk ->
      val blobId = treeWalk.getObjectId(0)
      REPO.newObjectReader().use { objectReader ->
        val objectLoader = objectReader.open(blobId)
        return String(objectLoader.bytes, StandardCharsets.UTF_8)
      }
    }
  } catch (e: IOException) {
    println(e)
  }
  return null
}
