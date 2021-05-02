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
  val logger = LogManager.getLogger()

  val projects = arrayOf(
    "pechu-thiramai",
    "Alamofire",
    "RxSwift",
    "kaathadi-ios",
    "swift",
    "TelegramSwift"
  )

  val projectPath = Paths.get("/Users/ragunathjawahar/OtherProjects/${projects[projects.size - 1]}")
  val sourceFiles = Files
    .find(projectPath, Integer.MAX_VALUE, BiPredicate { _, fileAttributes -> fileAttributes.isRegularFile })
    .asSequence()
    .filter { it.fileName.toString().endsWith(".swift") }
    .filter { !it.toAbsolutePath().toString().contains("/Pods/") }
    .toList()

  val parseResults = sourceFiles
    .asSequence()
    .onEach { logger.info("Inspecting file ${it.fileName}") }
    .map { Files.readAllBytes(it) }
    .map { String(it) }
    .map { snippet -> getParseResults(snippet, SwiftFunctionScanner) }
    .onEach { if (it.isEmpty()) logger.warn("No functions found, suspicious. PTAL?") }
    .filter { it.isNotEmpty() }
    .flatten()

  val endTime = System.currentTimeMillis()
  logger.info("Total number of scanned files: ${sourceFiles.size}")
  val parseResultsList = parseResults.toList()
  logger.info("Est. well-formed functions: ${parseResultsList.filterIsInstance<WellFormedFunction>().size}")
  logger.info("Est. malformed functions: ${parseResultsList.filterIsInstance<MalformedFunction>().size}")
  logger.info("Est. non-functions: ${parseResultsList.filterIsInstance<Nothing>().size}")
  logger.info("Analysis took: ${endTime - startTime}ms")
}
