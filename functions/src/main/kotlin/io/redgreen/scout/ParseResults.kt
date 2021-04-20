package io.redgreen.scout

import io.redgreen.scout.ParseResult.WellFormedFunction

fun getParseResults(
  scanner: (String) -> List<PossibleFunction>,
  snippet: String
): List<ParseResult> {
  val possibleFunctions = scanner(snippet)
  if (possibleFunctions.isEmpty()) {
    return emptyList()
  }

  val lineNumbers = possibleFunctions.map { it.startLineNumber }
  val functionSnippets = split(snippet, lineNumbers.first(), *lineNumbers.drop(1).toIntArray())
  val parseResults = functionSnippets
    .zip(lineNumbers)
    .map { (functionSnippet, lineNumber) -> parse(functionSnippet, lineNumber) }

  return possibleFunctions.zip(parseResults) { possibleFunction, result ->
    if (result is WellFormedFunction) {
      result.addName(possibleFunction.name)
    } else {
      result
    }
  }
}
