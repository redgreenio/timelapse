package xyz.ragunath.soso.swift

import xyz.ragunath.soso.PossibleFunction
import xyz.ragunath.soso.extensions.push

private val KEYWORD_FUNC = "func ".toCharArray()

private const val MODE_SEEK_FUNCTION = 0
private const val MODE_SEEK_FUNCTION_NAME = 1
private const val MODE_SEEK_OPEN_PARENTHESIS = 2
private const val MODE_SKIP_SINGLE_LINE_COMMENT = 3

fun swiftScan(code: String): List<PossibleFunction> {
  val possibleFunctions = mutableListOf<PossibleFunction>()
  val buffer = CharArray(KEYWORD_FUNC.size)
  val codeChars = code.toCharArray()
  var lineNumber = 1
  var possibleFunctionLineNumber = lineNumber
  var mode = MODE_SEEK_FUNCTION
  var previousMode = MODE_SEEK_FUNCTION
  val functionName = mutableListOf<Char>()

  for (char in codeChars) {
    buffer.push(char)

    if (buffer[buffer.size - 2] == '/' && buffer[buffer.size - 1] == '/') {
      previousMode = mode
      mode = MODE_SKIP_SINGLE_LINE_COMMENT
    }

    if (mode == MODE_SEEK_FUNCTION) {
      if (buffer.contentEquals(KEYWORD_FUNC)) {
        possibleFunctionLineNumber = lineNumber
        mode = MODE_SEEK_FUNCTION_NAME
      }
    }

    if (mode == MODE_SEEK_FUNCTION_NAME && char != ' ' && char != '(') {
      functionName.add(char)
    } else if (char == '(') {
      mode = MODE_SEEK_OPEN_PARENTHESIS
    }

    if (mode == MODE_SEEK_OPEN_PARENTHESIS && char == '}') {
      mode = MODE_SEEK_FUNCTION
      functionName.clear()
    }

    if (mode == MODE_SEEK_OPEN_PARENTHESIS && char == '{' && functionName.isNotEmpty()) {
      possibleFunctions.add(
        PossibleFunction(functionName.joinToString(""), possibleFunctionLineNumber)
      )
      mode = MODE_SEEK_FUNCTION
      functionName.clear()
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
