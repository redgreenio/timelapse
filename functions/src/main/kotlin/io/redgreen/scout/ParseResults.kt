package io.redgreen.scout

import io.redgreen.lang.kotlin.getFunctions
import io.redgreen.scout.ParseResult.WellFormedFunction
import io.redgreen.scout.languages.swift.SwiftFunctionScanner
import java.util.Optional

fun getParseResults(
  snippet: String,
  functionScanner: FunctionScanner
): List<ParseResult> {
  val functions = getFunctions(snippet)

  val possibleFunctions = functionScanner.scan(snippet) // Get rid of this one as well!
  if (possibleFunctions.isEmpty()) {
    return emptyList()
  }

  return if (functionScanner is SwiftFunctionScanner) {
    backwardCompatibleFunctionScanning(possibleFunctions, snippet)
  } else {
    functions
      .map { WellFormedFunction(Optional.of(Name(it.identifier)), it.startLine, it.endLine) }
  }
}

@Deprecated("The naive function scanning logic is no longer in use. Prefer ANTLR for parsing source code.")
private fun backwardCompatibleFunctionScanning(
  possibleFunctions: List<PossibleFunction>,
  snippet: String
): List<ParseResult> {
  val lineNumbers = possibleFunctions.map { it.startLineNumber }
  val functionSnippets = split(snippet, lineNumbers.first(), *lineNumbers.drop(1).toIntArray())
  val parseResults = functionSnippets
    .zip(lineNumbers)
    .map { (functionSnippet, lineNumber) -> parse(functionSnippet, lineNumber) }

  return possibleFunctions.zip(parseResults) { possibleFunction, result ->
    if (result is WellFormedFunction) {
      result.enrichWithNameAndStartLine(possibleFunction.name, possibleFunction.startLineNumber)
    } else {
      result
    }
  }
}
