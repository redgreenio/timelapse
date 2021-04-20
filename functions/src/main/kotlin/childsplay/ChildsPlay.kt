package childsplay

import io.redgreen.scout.ParseResult.WellFormedFunction
import io.redgreen.scout.getParseResults
import io.redgreen.scout.languages.swift.SwiftFunctionScanner
import java.nio.file.Files
import java.nio.file.Paths
import java.util.function.BiPredicate
import kotlin.streams.asSequence

fun main() {
  val projects = arrayOf(
    "kaathadi-ios",
    "pechu-thiramai",
    "Alamofire",
    "RxSwift",
    "TelegramSwift",
    "swift"
  )

  val projectPath = Paths.get("/Users/ragunathjawahar/OtherProjects/${projects[projects.size - 1]}")
  val functionsGroupedByLength = Files
    .find(projectPath, Integer.MAX_VALUE, BiPredicate { _, fileAttributes -> fileAttributes.isRegularFile })
    .asSequence()
    .filter { it.fileName.toString().endsWith(".swift") }
    .filter { !it.toAbsolutePath().toString().contains("/Pods/") }
    .map {
      println("Reading contents from $it")
      Files.readAllBytes(it)
    }
    .map { String(it) }
    .map { snippet -> getParseResults(SwiftFunctionScanner::scan, snippet) }
    .filter { it.isNotEmpty() }
    .asSequence()
    .toList()
    .flatten()
    .filterIsInstance<WellFormedFunction>()
    .groupBy { it.length }

  val functionLengths = functionsGroupedByLength
    .map { it.value }
    .flatten()
    .map { it.length }

  println("Total number of functions: ${functionLengths.size}")
  println(functionLengths.joinToString())
}
