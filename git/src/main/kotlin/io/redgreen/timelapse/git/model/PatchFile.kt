package io.redgreen.timelapse.git.model

class PatchFile(private val unifiedPatch: String) {
  enum class Side {
    A, B
  }

  companion object {
    private const val CHAR_NEWLINE = "\n"
    private const val CHAR_SPACE = " "
    private const val CHAR_PLUS = "+"
    private const val CHAR_MINUS = "-"
    private const val CHAR_FORWARD_SLASH = "\\"
    private const val HUNK_HEADER_PREFIX = "@@"

    private const val INDEX_SIDE_A = 1
    private const val INDEX_SIDE_B = 2

    fun from(unifiedPatch: String): PatchFile =
      PatchFile(unifiedPatch)
  }

  fun affectedLineNumbers(side: Side): List<Int> {
    val lines = unifiedPatch.split(CHAR_NEWLINE)

    val unifiedDiffHeaders = lines.filter { it.startsWith(HUNK_HEADER_PREFIX) }
    val hunkHeaders = unifiedDiffHeaders
      .map { diffHeader -> hunkHeader(diffHeader, side) }

    val hunkLinesList = unifiedDiffHeaders.mapIndexed { index, unifiedDiffHeader ->
      val unifiedDiffHeaderIndex = lines.indexOf(unifiedDiffHeader)
      val nextUnifiedDiffHeaderIndex = if (index == unifiedDiffHeaders.lastIndex) {
        lines.lastIndex
      } else {
        lines.indexOf(unifiedDiffHeaders[index + 1]) - 1
      }
      lines.subList(unifiedDiffHeaderIndex + 1, nextUnifiedDiffHeaderIndex)
    }

    val getAffectedLinesFunction = if (side == Side.A) ::affectedLinesForA else ::affectedLinesForB

    return hunkHeaders
      .zip(hunkLinesList, getAffectedLinesFunction)
      .flatten()
  }

  private fun affectedLinesForA(
    hunkHeader: HunkHeader,
    hunkLines: List<String>
  ): List<Int> {
    return affectedLines(hunkLines, hunkHeader, CHAR_PLUS) { it.startsWith(CHAR_MINUS) }
  }

  private fun affectedLinesForB(
    hunkHeader: HunkHeader,
    hunkLines: List<String>
  ): List<Int> {
    return affectedLines(hunkLines, hunkHeader, CHAR_MINUS) { it.startsWith(CHAR_PLUS) }
  }

  private fun affectedLines(
    hunkLines: List<String>,
    hunkHeader: HunkHeader,
    modificationChar: String,
    modificationCheck: (line: String) -> Boolean
  ): List<Int> {
    val affectedLineNumbers = mutableListOf<Int>()
    var offset = 0
    hunkLines.foldIndexed(affectedLineNumbers) { index, lineNumbersAccumulator, line ->
      if (line.startsWith(modificationChar) || line.startsWith(CHAR_FORWARD_SLASH)) {
        offset--
      } else if (modificationCheck(line)) {
        val lineNumber = offset + index + hunkHeader.startLine
        if (lineNumber <= hunkHeader.extendsTill) {
          lineNumbersAccumulator.add(lineNumber)
        }
      }
      lineNumbersAccumulator
    }
    return affectedLineNumbers.toList()
  }

  private fun hunkHeader(
    diffHeader: String,
    side: Side
  ): HunkHeader {
    val diffHeaderParts = diffHeader.split(CHAR_SPACE)
    val indexSide = if (side == Side.A) INDEX_SIDE_A else INDEX_SIDE_B
    return HunkHeader.from(diffHeaderParts[indexSide])
  }
}
