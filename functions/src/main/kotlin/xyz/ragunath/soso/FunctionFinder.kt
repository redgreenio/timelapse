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

// TODO(rj) 13/Oct/19 - Assertions for incoming parameters - line numbers, sorting, no dupes, etc.,
fun split(
  text: String,
  splitLineNumber: Int,
  vararg moreSplitLineNumbers: Int
): List<String> {
  val lines = text.split('\n')
  require(splitLineNumber <= lines.size) { "`splitLineNumber`: $splitLineNumber cannot be greater than the number of lines: ${lines.size}" }

  if (lines.size == 1) {
    return listOf(text)
  }

  val splitLineNumbers = listOf(splitLineNumber, *moreSplitLineNumbers.toTypedArray())
  return getSplitRanges(splitLineNumbers, lines.size)
    .map { (startLine, endLine) -> splitRange(lines, startLine, endLine) }
    .toList()
}

private fun getSplitRanges(
  numbers: List<Int>,
  numberOfLines: Int
): List<Pair<Int, Int>> {
  var splitRanges = numbers
    .mapIndexed { index, number ->
      if (index == numbers.size - 1) {
        number to (numberOfLines + 1)
      } else {
        number to numbers[index + 1]
      }
    }
  val startingLine = splitRanges.first().first
  if (startingLine != 1) {
    splitRanges = listOf(1 to startingLine, *splitRanges.toTypedArray())
  }
  return splitRanges
}

private fun splitRange(
  lines: List<String>,
  startLineNumber: Int,
  endLineNumber: Int
): String {
  return lines
    .filterIndexed { index, _ -> (index + 1) >= startLineNumber && (index + 1) < endLineNumber }
    .joinToString("\n")
}
