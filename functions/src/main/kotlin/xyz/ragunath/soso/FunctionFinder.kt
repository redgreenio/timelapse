package xyz.ragunath.soso

fun findPossibleFunctions(snippet: String): List<PossibleFunction> {
  if (snippet.isBlank()) return emptyList()

  val possibleFunctions = mutableListOf<PossibleFunction>()
  var lineNumber = 1
  val lines = snippet.split('\n')
  for (line in lines) {
    if (line.contains("fun")) {
      val endOfFunctionKeyword = line.indexOf("fun") + "fun".length
      val indexOfParentheses = line.indexOf('(')
      val name = line.substring(endOfFunctionKeyword, indexOfParentheses).trim()
      possibleFunctions.add(PossibleFunction(lineNumber, name))
    }
    lineNumber++
  }

  return possibleFunctions.toList()
}

fun split(text: String, splitLineNumber: Int): String {
  val lines = text.split('\n')
  require(splitLineNumber <= lines.size) { "`splitLineNumber`: $splitLineNumber cannot be greater than the number of lines: ${lines.size}" }
  return text
}
