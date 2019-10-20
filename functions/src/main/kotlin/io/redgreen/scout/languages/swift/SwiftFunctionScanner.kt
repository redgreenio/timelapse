package io.redgreen.scout.languages.swift

import io.redgreen.scout.FunctionScanner
import io.redgreen.scout.PossibleFunction
import io.redgreen.scout.extensions.endsWith
import io.redgreen.scout.extensions.push
import io.redgreen.scout.languages.swift.SwiftFunctionScanner.ScanMode.SEEK_FUNCTION
import io.redgreen.scout.languages.swift.SwiftFunctionScanner.ScanMode.SEEK_FUNCTION_NAME
import io.redgreen.scout.languages.swift.SwiftFunctionScanner.ScanMode.SEEK_OPEN_PARENTHESIS
import io.redgreen.scout.languages.swift.SwiftFunctionScanner.ScanMode.SKIP_SINGLE_LINE_COMMENT

object SwiftFunctionScanner : FunctionScanner {
  private val KEYWORD_FUNC = "func ".toCharArray()
  private val KEYWORD_INIT = "init".toCharArray()
  private val TOKEN_SINGLE_LINE_COMMENT = "//".toCharArray()

  private enum class ScanMode {
    SEEK_FUNCTION,
    SEEK_FUNCTION_NAME,
    SEEK_OPEN_PARENTHESIS,
    SKIP_SINGLE_LINE_COMMENT
  }

  override fun scan(snippet: String): List<PossibleFunction> {
    val possibleFunctions = mutableListOf<PossibleFunction>()
    val buffer = CharArray(KEYWORD_FUNC.size)
    val snippetChars = snippet.toCharArray()
    var lineNumber = 1
    var possibleLineNumber = lineNumber
    var mode = SEEK_FUNCTION
    var previousMode = SEEK_FUNCTION
    val functionNameChars = mutableListOf<Char>()

    for (char in snippetChars) {
      buffer.push(char)

      if (buffer.endsWith(TOKEN_SINGLE_LINE_COMMENT)) {
        previousMode = mode
        mode = SKIP_SINGLE_LINE_COMMENT
      }

      if (mode == SEEK_FUNCTION) {
        if (buffer.contentEquals(KEYWORD_FUNC)) {
          possibleLineNumber = lineNumber
          mode = SEEK_FUNCTION_NAME
        } else if (buffer.endsWith(KEYWORD_INIT)) {
          possibleLineNumber = lineNumber
          functionNameChars.addAll(KEYWORD_INIT.toTypedArray())
          mode = SEEK_OPEN_PARENTHESIS
        }
      }

      if (mode == SEEK_FUNCTION_NAME && char != ' ' && char != '(') {
        functionNameChars.add(char)
      } else if (char == '(' && mode != SEEK_FUNCTION) {
        mode = SEEK_OPEN_PARENTHESIS
      }

      if (mode == SEEK_OPEN_PARENTHESIS && char == '}') {
        mode = SEEK_FUNCTION
        functionNameChars.clear()
      }

      if (mode == SEEK_OPEN_PARENTHESIS && char == '{' && functionNameChars.isNotEmpty()) {
        val possibleFunctionName = functionNameChars.joinToString("")
        possibleFunctions.add(PossibleFunction(possibleFunctionName, possibleLineNumber))
        mode = SEEK_FUNCTION
        functionNameChars.clear()
      }

      if (char == '\n') {
        lineNumber++
        if (mode == SKIP_SINGLE_LINE_COMMENT) {
          mode = previousMode
        }
      }
    }

    return possibleFunctions.toList()
  }
}
