package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.FunctionScanner
import io.redgreen.scout.ParseResult.WellFormedFunction
import io.redgreen.scout.getParseResults
import io.redgreen.scout.split
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified

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
    .filter { functionContentsChanged(beforeSource, functionsInBefore, afterSource, it) }
    .map { Modified(it, snippet(afterSource, it.startLine, it.endLine)) }

  return addedResults + deletedResults + modifiedResults
}

private fun functionContentsChanged(
  beforeSource: String,
  functionsInBefore: List<WellFormedFunction>,
  afterSource: String,
  supposedlyModifiedFunction: WellFormedFunction
): Boolean {
  val functionFromBeforeSource = functionsInBefore
    .find { it.name == supposedlyModifiedFunction.name }
    ?: return true

  val beforeStartLine = functionFromBeforeSource.startLine
  val beforeEndLine = functionFromBeforeSource.endLine
  val beforeFunctionSnippet = snippet(beforeSource, beforeStartLine, beforeEndLine)

  val afterStartLine = supposedlyModifiedFunction.startLine
  val afterEndLine = supposedlyModifiedFunction.endLine
  val afterFunctionSnippet = snippet(afterSource, afterStartLine, afterEndLine)

  return beforeFunctionSnippet != afterFunctionSnippet
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
  return getParseResults(source, scanner::scan).filterIsInstance<WellFormedFunction>()
}
