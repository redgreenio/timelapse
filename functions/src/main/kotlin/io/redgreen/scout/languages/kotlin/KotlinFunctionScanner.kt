package io.redgreen.scout.languages.kotlin

import io.redgreen.scout.FunctionScanner
import io.redgreen.scout.PossibleFunction

object KotlinFunctionScanner : FunctionScanner {
  private const val KEYWORD = "fun "

  override fun scan(snippet: String): List<PossibleFunction> {
    if (snippet.isBlank()) return emptyList()

    val possibleFunctions = mutableListOf<PossibleFunction>()
    var lineNumber = 1
    val lines = snippet.split('\n')
    for (line in lines) {
      if (line.contains(KEYWORD)) {
        val endOfFunctionKeyword = line.indexOf(KEYWORD) + KEYWORD.length
        val indexOfParentheses = line.indexOf('(')
        val name = line.substring(endOfFunctionKeyword, indexOfParentheses).trim()
        possibleFunctions.add(PossibleFunction(name, lineNumber))
      }
      lineNumber++
    }

    return possibleFunctions.toList()
  }
}
