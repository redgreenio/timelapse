@file:SuppressWarnings("TooManyFunctions")

package toys

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.treewalk.TreeWalk
import org.jetbrains.kotlin.psi.KtFile
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
  rxComputeMetrics()
}

private fun rxComputeMetrics() {
  var startMillis = 0L
  var endTimeMillis = 0L

  val allOfIt = Observable
    .fromIterable(getCommitHashesAndLoc())
    .flatMap { Observable.fromCallable { it to getContentAtRevision(it.first) }.subscribeOn(Schedulers.io()) }
    .map { Observable.fromCallable { computeMetrics(it.first, it.second) } }
    .flatMap { it.subscribeOn(Schedulers.computation()) }
    .toList()
    .doOnSubscribe { startMillis = System.currentTimeMillis() }
    .doOnSuccess {
      endTimeMillis = System.currentTimeMillis()
    }.blockingGet()

  printOutput(allOfIt)
  println("\nRx took ${endTimeMillis - startMillis}ms.")
}

@SuppressWarnings("unused", "UnusedPrivateMember")
private fun plainComputeMetrics() {
  val timeInMillis = measureTimeMillis {
    val allOfIt = getCommitHashesAndLoc()
      .map { commitHashAndLoc -> commitHashAndLoc to getContentAtRevision(commitHashAndLoc.first) }
      .map { computeMetrics(it.first, it.second) }
    printOutput(allOfIt)
  }
  println("\nPlain took ${timeInMillis}ms.\n")
}

private fun printOutput(allOfIt: List<Pair<String, Triple<Int, Int, Int>>>) {
  allOfIt.onEach { (commitHash, metrics) ->
    val (loc, cc, mcc) = metrics
    val paddedLoc = loc.toString().padStart(PADDING)
    val paddedCognitive = cc.toString().padStart(PADDING)
    val paddedCyclomatic = mcc.toString().padStart(PADDING)
    println("$commitHash || $paddedLoc @@ $paddedCognitive ^^ $paddedCyclomatic")
  }
}

private fun computeMetrics(
  commitHashLocPair: Pair<String, Int>,
  content: String
): Pair<String, Triple<Int, Int, Int>> {
  val (commitHash, loc) = commitHashLocPair

  val ktFile = createKtFile(content)
  val cognitiveComplexity = computeCognitiveComplexity(ktFile)
  val cyclomaticComplexity = computeCyclomaticComplexity(ktFile)

  val metrics = Triple(loc, cognitiveComplexity, cyclomaticComplexity)
  return commitHash to metrics
}

private fun computeCyclomaticComplexity(ktFile: KtFile): Int {
  val cyclomaticComplexityVisitor = CyclomaticComplexityVisitor()
  ktFile.accept(cyclomaticComplexityVisitor)
  return cyclomaticComplexityVisitor.complexity
}

private fun computeCognitiveComplexity(ktFile: KtFile): Int {
  val cognitiveComplexityVisitor = CognitiveComplexityVisitor()
  ktFile.accept(cognitiveComplexityVisitor)
  return cognitiveComplexityVisitor.complexity
}

private fun createKtFile(content: String): KtFile =
  PSI_FACTORY.createFile("FileUnderAnalysis.kt", content)

private fun getContentAtRevision(commitHash: String): String {
  val revCommit = REPO.parseCommit(ObjectId.fromString(commitHash))
  return getContent(revCommit)!!
}

@SuppressWarnings("MagicNumber")
private fun getCommitHashesAndLoc(): List<Pair<String, Int>> {
  val args = listOf(
    "git",
    "--git-dir",
    REPO_PATH,
    "log",
    "--stat",
    "--pretty=oneline",
    "--follow",
    "--",
    FILE_PATH,
  )
  val rawText = Runtime.getRuntime().exec(args.toTypedArray())
    .inputStream
    .reader()
    .use { it.readText().trim() }

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
  try {
    TreeWalk.forPath(REPO, FILE_PATH, commit.tree).use { treeWalk ->
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
