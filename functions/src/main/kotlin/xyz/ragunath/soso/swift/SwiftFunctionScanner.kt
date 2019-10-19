package xyz.ragunath.soso.swift

import xyz.ragunath.soso.FunctionScanner
import xyz.ragunath.soso.PossibleFunction
import xyz.ragunath.soso.extensions.push

private val KEYWORD_FUNC = "func ".toCharArray()

private const val MODE_SEEK_FUNCTION = 0
private const val MODE_SEEK_FUNCTION_NAME = 1
private const val MODE_SEEK_OPEN_PARENTHESIS = 2
private const val MODE_SKIP_SINGLE_LINE_COMMENT = 3

class SwiftFunctionScanner : FunctionScanner {
  override fun scan(snippet: String): List<PossibleFunction> {
    val possibleFunctions = mutableListOf<PossibleFunction>()
    val buffer = CharArray(KEYWORD_FUNC.size)
    val snippetChars = snippet.toCharArray()
    var lineNumber = 1
    var possibleLineNumber = lineNumber
    var mode = MODE_SEEK_FUNCTION
    var previousMode = MODE_SEEK_FUNCTION
    val functionNameChars = mutableListOf<Char>()

    for (char in snippetChars) {
      buffer.push(char)

      if (buffer[buffer.size - 2] == '/' && buffer[buffer.size - 1] == '/') {
        previousMode = mode
        mode = MODE_SKIP_SINGLE_LINE_COMMENT
      }

      if (mode == MODE_SEEK_FUNCTION) {
        if (buffer.contentEquals(KEYWORD_FUNC)) {
          possibleLineNumber = lineNumber
          mode = MODE_SEEK_FUNCTION_NAME
        }
      }

      if (mode == MODE_SEEK_FUNCTION_NAME && char != ' ' && char != '(') {
        functionNameChars.add(char)
      } else if (char == '(') {
        mode = MODE_SEEK_OPEN_PARENTHESIS
      }

      if (mode == MODE_SEEK_OPEN_PARENTHESIS && char == '}') {
        mode = MODE_SEEK_FUNCTION
        functionNameChars.clear()
      }

      if (mode == MODE_SEEK_OPEN_PARENTHESIS && char == '{' && functionNameChars.isNotEmpty()) {
        val possibleFunctionName = functionNameChars.joinToString("")
        possibleFunctions.add(PossibleFunction(possibleFunctionName, possibleLineNumber))
        mode = MODE_SEEK_FUNCTION
        functionNameChars.clear()
      }

      if (char == '\n') {
        lineNumber++
        if (mode == MODE_SKIP_SINGLE_LINE_COMMENT) {
          mode = previousMode
        }
      }
    }

    return possibleFunctions.toList()
  }
}
