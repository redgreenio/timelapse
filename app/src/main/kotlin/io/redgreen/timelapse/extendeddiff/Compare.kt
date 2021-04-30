package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.FunctionScanner
import io.redgreen.scout.ParseResult.WellFormedFunction
import io.redgreen.scout.getParseResults
import io.redgreen.scout.split
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Renamed
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Unmodified

typealias FunctionBody = String
typealias FunctionName = String

internal fun compare(
  beforeSource: String,
  afterSource: String,
  scanner: FunctionScanner
): List<ComparisonResult> {
  val functionsInBefore = getWellFormedFunctions(beforeSource, scanner)
  val functionsInAfter = getWellFormedFunctions(afterSource, scanner)

  val functionNamesInBefore = functionsInBefore.map(WellFormedFunction::name)
  val functionNamesInAfter = functionsInAfter.map(WellFormedFunction::name)

  val addedFunctionNames = functionNamesInAfter - functionNamesInBefore
  val deletedFunctionNames = functionNamesInBefore - functionNamesInAfter
  val modifiedFunctionNames = deletedFunctionNames.intersect(addedFunctionNames)
  val possiblyUnchangedFunctionNames = functionNamesInAfter - addedFunctionNames

  val addedFunctions = functionsInAfter
    .filter { addedFunctionNames.contains(it.name) }
    .toMutableList()

  val deletedFunctions = functionsInBefore
    .filter { deletedFunctionNames.contains(it.name) }
    .toMutableList()

  val renamedFunctionNames = getRenamedFunctionNames(addedFunctions, afterSource, deletedFunctions, beforeSource)

  val renamedResults = mutableListOf<Renamed>()

  renamedFunctionNames.onEach { (addedFunctionName, deletedFunctionName) ->
    val indexOfAddedFunctionToRemove = addedFunctions.indexOfFirst { it.name.get().value == addedFunctionName }
    addedFunctions.removeAt(indexOfAddedFunctionToRemove)

    val indexOfDeletedFunctionToRemove = deletedFunctions.indexOfFirst { it.name.get().value == deletedFunctionName }
    deletedFunctions.removeAt(indexOfDeletedFunctionToRemove)

    val renamedFunction = functionsInAfter.find { it.name.get().value == addedFunctionName }!!
    renamedResults.add(Renamed(renamedFunction, deletedFunctionName))
  }

  val modifiedFunctions = functionsInAfter
    .filter { modifiedFunctionNames.contains(it.name) }

  val possiblyUnchangedFunctions = functionsInAfter
    .filter { possiblyUnchangedFunctionNames.contains(it.name) }

  val addedResults = addedFunctions
    .filter { it.name !in modifiedFunctionNames }
    .map(::Added)

  val deletedResults = deletedFunctions
    .filter { it.name !in modifiedFunctionNames }
    .map { Deleted(it, snippet(beforeSource, it.startLine, it.endLine)) }

  val modifiedResults = modifiedFunctions
    .filter { functionContentsChanged(beforeSource, functionsInBefore, afterSource, it) }
    .map { Modified(it, snippet(afterSource, it.startLine, it.endLine)) }

  val modifiedFunctionsNoLineNumberChange = possiblyUnchangedFunctions
    .filter { functionContentsChanged(beforeSource, functionsInBefore, afterSource, it) }

  val modifiedNoLineNumberChangeResults = modifiedFunctionsNoLineNumberChange
    .map { Modified(it, snippet(afterSource, it.startLine, it.endLine)) }

  val unmodifiedFunctions = possiblyUnchangedFunctions - modifiedFunctionsNoLineNumberChange
  val unmodifiedResults = unmodifiedFunctions.map(::Unmodified)

  return addedResults + deletedResults +
    modifiedResults + modifiedNoLineNumberChangeResults +
    unmodifiedResults +
    renamedResults
}

fun getRenamedFunctionNames(
  addedFunctions: List<WellFormedFunction>,
  afterSource: String,
  deletedFunctions: List<WellFormedFunction>,
  beforeSource: String
): List<Pair<FunctionName, FunctionName>> {
  val addedFunctionBodiesAndNames = functionBodiesAndNames(afterSource, addedFunctions)
  val deletedFunctionBodiesAndNames = functionBodiesAndNames(beforeSource, deletedFunctions)

  val identicalFunctionBodies = addedFunctionBodiesAndNames.map(Pair<String, String>::first)
    .intersect(deletedFunctionBodiesAndNames.map(Pair<String, String>::first))

  return identicalFunctionBodies.map { identicalBody ->
    val addedFunctionName = addedFunctionBodiesAndNames
      .find { (addedFunctionBody, _) -> addedFunctionBody == identicalBody }!!.second
    val deletedFunctionName = deletedFunctionBodiesAndNames
      .find { (deletedFunctionBody, _) -> deletedFunctionBody == identicalBody }!!.second

    addedFunctionName to deletedFunctionName
  }
}

private fun functionBodiesAndNames(
  source: String,
  functionsToLookup: List<WellFormedFunction>
): List<Pair<FunctionBody, FunctionName>> {
  return functionsToLookup
    .map { getFunctionBody(it, source) to it.name.get().value }
}

private fun getFunctionBody(
  wellFormedFunction: WellFormedFunction,
  afterSource: String
): String {
  val functionWithSignature = split(afterSource, wellFormedFunction.startLine, wellFormedFunction.endLine)
    .joinToString("\n")
  return functionWithSignature
    .substring(functionWithSignature.indexOf("{"), functionWithSignature.length)
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
