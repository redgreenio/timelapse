package io.redgreen.scout

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
  splitLineNumbers: List<Int>,
  totalLines: Int
): List<Pair<Int, Int>> {
  return splitLineNumbers
    .mapIndexed { index, number ->
      if (index == splitLineNumbers.size - 1) {
        number to (totalLines + 1)
      } else {
        number to splitLineNumbers[index + 1]
      }
    }
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
