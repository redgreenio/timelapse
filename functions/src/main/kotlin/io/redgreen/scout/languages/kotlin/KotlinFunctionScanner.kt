package io.redgreen.scout.languages.kotlin

import io.redgreen.scout.FunctionScanner
import io.redgreen.scout.PossibleFunction
import kotlin.streams.toList

object KotlinFunctionScanner : FunctionScanner {
  private const val KEYWORD_FUN = "fun "

  private const val CHAR_LEFT_PARENTHESES = '('
  private const val CHAR_QUOTE = '"'

  override fun scan(snippet: String): List<PossibleFunction> {
    if (snippet.isBlank()) return emptyList()

    val possibleFunctions = mutableListOf<PossibleFunction>()
    var lineNumber = 1
    val lines = snippet.split('\n')
    for (line in lines) {
      val containsFunctionSignature = line.contains(KEYWORD_FUN) && line.indexOf(CHAR_LEFT_PARENTHESES) != -1
      if (containsFunctionSignature && isNotStringLiteral(line)) {
        val endOfFunctionKeyword = line.indexOf(KEYWORD_FUN) + KEYWORD_FUN.length
        val indexOfParentheses = line.indexOf(CHAR_LEFT_PARENTHESES)
        val name = line.substring(endOfFunctionKeyword, indexOfParentheses).trim()
        possibleFunctions.add(PossibleFunction(name, lineNumber))
      }
      lineNumber++
    }

    return possibleFunctions.toList()
  }

  private fun isNotStringLiteral(line: String): Boolean {
    val lineChars = line.chars().toList()
    val funKeywordIndex = line.indexOf(KEYWORD_FUN)

    val quoteCharsCount = lineChars
      .map(Int::toChar)
      .take(funKeywordIndex + 1)
      .filter { it == CHAR_QUOTE }
      .count()

    return quoteCharsCount % 2 == 0
  }
}
