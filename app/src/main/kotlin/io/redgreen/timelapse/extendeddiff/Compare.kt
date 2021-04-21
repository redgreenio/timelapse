package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.FunctionScanner
import io.redgreen.scout.ParseResult.WellFormedFunction
import io.redgreen.scout.getParseResults
import io.redgreen.scout.split
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified

fun patchAndCompare(
  source: String,
  patch: String,
  scanner: FunctionScanner
): List<ComparisonResult> =
  compare(source, applyPatch(source, patch), scanner)

internal fun compare(
  beforeSource: String,
  afterSource: String,
  scanner: FunctionScanner
): List<ComparisonResult> {
  val functionsInBefore = getWellFormedFunctions(beforeSource, scanner)
  val functionsInAfter = getWellFormedFunctions(afterSource, scanner)

  val addedFunctions = functionsInAfter - functionsInBefore
  val deletedFunctions = functionsInBefore - functionsInAfter

  val addedFunctionNames = addedFunctions.map(WellFormedFunction::name)
  val deletedFunctionName = deletedFunctions.map(WellFormedFunction::name)
  val modifiedFunctionNames = deletedFunctionName.intersect(addedFunctionNames)
  val modifiedFunctions = functionsInAfter.filter { modifiedFunctionNames.contains(it.name) }

  val addedResults = addedFunctions
    .filter { it.name !in modifiedFunctionNames }
    .map(::Added)

  val deletedResults = deletedFunctions
    .filter { it.name !in modifiedFunctionNames }
    .map { Deleted(it, snippet(beforeSource, it.startLine, it.endLine)) }

  val modifiedResults = modifiedFunctions
    .map(::Modified)

  return addedResults + deletedResults + modifiedResults
}

private fun snippet(
  source: String,
  startLine: Int,
  endLine: Int
): String {
  return split(source, startLine, endLine + 1)
    .first()
}

private fun getWellFormedFunctions(
  source: String,
  scanner: FunctionScanner
): List<WellFormedFunction> {
  return getParseResults(scanner::scan, source).filterIsInstance<WellFormedFunction>()
}
