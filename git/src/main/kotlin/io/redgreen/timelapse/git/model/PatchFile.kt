package io.redgreen.timelapse.git.model

class PatchFile(private val unifiedPatch: String) {
  companion object {
    private const val CHAR_NEWLINE = "\n"
    private const val CHAR_SPACE = " "
    private const val CHAR_PLUS = "+"

    private const val HEADER_LINE_COUNT = 3
    private const val INDEX_LINE_UNIFIED_DIFF_HEADER = 2
    private const val INDEX_PART_HUNK_B = 2

    fun from(unifiedPatch: String): PatchFile =
      PatchFile(unifiedPatch)
  }

  fun getAffectedLineNumbers(): List<Int> {
    val lines = unifiedPatch.split(CHAR_NEWLINE)
    val unifiedDiffHeader = lines[INDEX_LINE_UNIFIED_DIFF_HEADER]
    val headerParts = unifiedDiffHeader.split(CHAR_SPACE)
    val hunkHeaderB = HunkHeader.from(headerParts[INDEX_PART_HUNK_B])
    val hunkLines = lines.drop(HEADER_LINE_COUNT)

    val affectedLineNumbers = mutableListOf<Int>()
    var offset = 0
    hunkLines.foldIndexed(affectedLineNumbers) { index, lineNumbersAccumulator, line ->
      if (line.startsWith("-") || line.startsWith("\\")) {
        offset--
      } else if (line.startsWith(CHAR_PLUS)) {
        lineNumbersAccumulator.add(offset + index + hunkHeaderB.startLine)
      }
      lineNumbersAccumulator
    }
    return affectedLineNumbers.toList()
  }
}

class HunkHeader(val startLine: Int) {
  companion object {
    private const val COMMA = ","

    fun from(hunkHeaderText: String): HunkHeader {
      val hunkHeaderTextSplit = hunkHeaderText.split(COMMA)
      val startLine = hunkHeaderTextSplit.first().toString().drop(1).toInt()
      return HunkHeader(startLine)
    }
  }
}
