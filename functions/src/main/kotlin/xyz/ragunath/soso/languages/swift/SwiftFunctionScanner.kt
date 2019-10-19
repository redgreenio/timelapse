package xyz.ragunath.soso.languages.swift

import xyz.ragunath.soso.FunctionScanner
import xyz.ragunath.soso.PossibleFunction
import xyz.ragunath.soso.extensions.push
import xyz.ragunath.soso.languages.swift.SwiftFunctionScanner.ScanMode.SEEK_FUNCTION
import xyz.ragunath.soso.languages.swift.SwiftFunctionScanner.ScanMode.SEEK_FUNCTION_NAME
import xyz.ragunath.soso.languages.swift.SwiftFunctionScanner.ScanMode.SEEK_OPEN_PARENTHESIS
import xyz.ragunath.soso.languages.swift.SwiftFunctionScanner.ScanMode.SKIP_SINGLE_LINE_COMMENT

object SwiftFunctionScanner : FunctionScanner {
  private val KEYWORD = "func ".toCharArray()

  private enum class ScanMode {
    SEEK_FUNCTION,
    SEEK_FUNCTION_NAME,
    SEEK_OPEN_PARENTHESIS,
    SKIP_SINGLE_LINE_COMMENT
  }

  override fun scan(snippet: String): List<PossibleFunction> {
    val possibleFunctions = mutableListOf<PossibleFunction>()
    val buffer = CharArray(KEYWORD.size)
    val snippetChars = snippet.toCharArray()
    var lineNumber = 1
    var possibleLineNumber = lineNumber
    var mode = SEEK_FUNCTION
    var previousMode = SEEK_FUNCTION
    val functionNameChars = mutableListOf<Char>()

    for (char in snippetChars) {
      buffer.push(char)

      if (buffer[buffer.size - 2] == '/' && buffer[buffer.size - 1] == '/') {
        previousMode = mode
        mode = SKIP_SINGLE_LINE_COMMENT
      }

      if (mode == SEEK_FUNCTION) {
        if (buffer.contentEquals(KEYWORD)) {
          possibleLineNumber = lineNumber
          mode = SEEK_FUNCTION_NAME
        }
      }

      if (mode == SEEK_FUNCTION_NAME && char != ' ' && char != '(') {
        functionNameChars.add(char)
      } else if (char == '(') {
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
