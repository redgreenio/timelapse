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

fun split(text: String, splitLineNumber: Int): List<String> {
  val lines = text.split('\n')
  require(splitLineNumber <= lines.size) { "`splitLineNumber`: $splitLineNumber cannot be greater than the number of lines: ${lines.size}" }

  if (lines.size > 1) {
    val splitLines = mutableListOf<String>()
    splitLines.add(lines.first())
    val remainingLines = lines.filterIndexed { index, _ -> index >= splitLineNumber - 1 }.joinToString("\n")
    splitLines.add(remainingLines)
    lines.first()
    return splitLines.toList()
  }

  return listOf(text)
}
