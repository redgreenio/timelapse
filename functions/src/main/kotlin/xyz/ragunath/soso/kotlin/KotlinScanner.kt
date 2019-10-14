package xyz.ragunath.soso.kotlin

import xyz.ragunath.soso.PossibleFunction

private const val FUNCTION_KEYWORD = "fun"

fun kotlinScan(snippet: String): List<PossibleFunction> {
  if (snippet.isBlank()) return emptyList()

  val possibleFunctions = mutableListOf<PossibleFunction>()
  var lineNumber = 1
  val lines = snippet.split('\n')
  for (line in lines) {
    if (line.contains(FUNCTION_KEYWORD)) {
      val endOfFunctionKeyword = line.indexOf(FUNCTION_KEYWORD) + FUNCTION_KEYWORD.length
      val indexOfParentheses = line.indexOf('(')
      val name = line.substring(endOfFunctionKeyword, indexOfParentheses).trim()
      possibleFunctions.add(PossibleFunction(name, lineNumber))
    }
    lineNumber++
  }

  return possibleFunctions.toList()
}
