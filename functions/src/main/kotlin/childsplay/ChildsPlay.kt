package childsplay

import io.redgreen.scout.ParseResult.MalformedFunction
import io.redgreen.scout.ParseResult.Nothing
import io.redgreen.scout.ParseResult.WellFormedFunction
import io.redgreen.scout.getParseResults
import io.redgreen.scout.languages.swift.SwiftFunctionScanner
import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import java.nio.file.Paths
import java.util.function.BiPredicate
import kotlin.streams.asSequence

fun main() {
  val startTime = System.currentTimeMillis()
  val projects = arrayOf(
    "pechu-thiramai",
    "Alamofire",
    "RxSwift",
    "TelegramSwift",
    "swift",
    "kaathadi-ios"
  )

  val projectPath = Paths.get("/Users/ragunathjawahar/OtherProjects/${projects[projects.size - 1]}")
  val parseResults = Files
    .find(projectPath, Integer.MAX_VALUE, BiPredicate { _, fileAttributes -> fileAttributes.isRegularFile })
    .asSequence()
    .filter { it.fileName.toString().endsWith(".swift") }
    .filter { !it.toAbsolutePath().toString().contains("/Pods/") }
    .map { Files.readAllBytes(it) }
    .map { String(it) }
    .map { snippet -> getParseResults(SwiftFunctionScanner::scan, snippet) }
    .filter { it.isNotEmpty() }
    .asSequence()
    .toList()
    .flatten()

  val endTime = System.currentTimeMillis()
  val logger = LogManager.getLogger()
  logger.info("Est. well-formed functions: ${parseResults.filterIsInstance<WellFormedFunction>().size}")
  logger.info("Est. malformed functions: ${parseResults.filterIsInstance<MalformedFunction>().size}")
  logger.info("Est. non-functions: ${parseResults.filterIsInstance<Nothing>().size}")
  logger.info("Analysis took: ${endTime - startTime}ms")
}
