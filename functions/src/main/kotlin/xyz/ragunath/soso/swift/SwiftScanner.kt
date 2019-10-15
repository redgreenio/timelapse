package xyz.ragunath.soso.swift

import xyz.ragunath.soso.PossibleFunction
import xyz.ragunath.soso.extensions.push

private const val KEYWORD_FUNC = "func "

private const val MODE_SEEK_FUNCTION = 0
private const val MODE_SEEK_FUNCTION_NAME = 1
private const val MODE_SEEK_OPEN_PARENTHESIS = 2

fun swiftScan(code: String): List<PossibleFunction> {
  val possibleFunctions = mutableListOf<PossibleFunction>()
  val buffer = CharArray(KEYWORD_FUNC.length)
  val codeChars = code.toCharArray()
  var lineNumber = 1
  var possibleFunctionLineNumber = lineNumber
  var mode = MODE_SEEK_FUNCTION
  val functionName = mutableListOf<Char>()

  for (char in codeChars) {
    buffer.push(char)

    if (mode == MODE_SEEK_FUNCTION) {
      if (String(buffer) == KEYWORD_FUNC) { // FIXME, this will cause too many allocations and GC
        possibleFunctionLineNumber = lineNumber
        mode = MODE_SEEK_FUNCTION_NAME
      }
    }

    if (char != ' ' && char != '(' && mode == MODE_SEEK_FUNCTION_NAME) {
      functionName.add(char)
    } else if (char == '(') {
      mode = MODE_SEEK_OPEN_PARENTHESIS
    }

    if (char == '{' && mode == MODE_SEEK_OPEN_PARENTHESIS) {
      possibleFunctions.add(
        PossibleFunction(functionName.joinToString(""), possibleFunctionLineNumber)
      )
      mode = MODE_SEEK_FUNCTION
      functionName.clear()
    }

    if (char == '\n') {
      lineNumber++
    }
  }

  return possibleFunctions.toList()
}
