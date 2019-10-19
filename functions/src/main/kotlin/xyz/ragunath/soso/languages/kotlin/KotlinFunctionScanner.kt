package xyz.ragunath.soso.languages.kotlin

import xyz.ragunath.soso.FunctionScanner
import xyz.ragunath.soso.PossibleFunction

class KotlinFunctionScanner : FunctionScanner {
  private val keyword = "fun"

  override fun scan(snippet: String): List<PossibleFunction> {
    if (snippet.isBlank()) return emptyList()

    val possibleFunctions = mutableListOf<PossibleFunction>()
    var lineNumber = 1
    val lines = snippet.split('\n')
    for (line in lines) {
      if (line.contains(keyword)) {
        val endOfFunctionKeyword = line.indexOf(keyword) + keyword.length
        val indexOfParentheses = line.indexOf('(')
        val name = line.substring(endOfFunctionKeyword, indexOfParentheses).trim()
        possibleFunctions.add(PossibleFunction(name, lineNumber))
      }
      lineNumber++
    }

    return possibleFunctions.toList()
  }
}
