package xyz.ragunath.soso.swift

import xyz.ragunath.soso.PossibleFunction

private const val KEYWORD_FUNC = "func "

fun swiftScan(code: String): List<PossibleFunction> {
  val possibleFunctions = mutableListOf<PossibleFunction>()
  val lines = code.lines()
  var lineNumber = 1

  for (line in lines) {
    if (line.contains(KEYWORD_FUNC)) {
      val startIndex = line.indexOf(KEYWORD_FUNC)
      val functionName = line.substring(startIndex + KEYWORD_FUNC.length, line.indexOf("(", startIndex))
      if (line.trim().endsWith('{')) {
        possibleFunctions.add(PossibleFunction(functionName, lineNumber))
      } else {
        println("Found `$functionName` on line $lineNumber. Excluding from result, PTAL?")
      }
    }
    lineNumber++
  }

  return possibleFunctions.toList()
}