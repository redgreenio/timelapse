package io.redgreen.timelapse.git.model

class PatchFile(private val unifiedPatch: String) {
  companion object {
    private const val CHAR_NEWLINE = "\n"
    private const val CHAR_SPACE = " "
    private const val CHAR_PLUS = "+"
    private const val CHAR_MINUS = "-"
    private const val CHAR_FORWARD_SLASH = "\\"
    private const val HUNK_HEADER_PREFIX = "@@"

    private const val INDEX_PART_HUNK_B = 2

    fun from(unifiedPatch: String): PatchFile =
      PatchFile(unifiedPatch)
  }

  fun getAffectedLineNumbers(): List<Int> {
    val lines = unifiedPatch.split(CHAR_NEWLINE)

    val unifiedDiffHeaders = lines.filter { it.startsWith(HUNK_HEADER_PREFIX) }
    val hunkBHeaders = unifiedDiffHeaders.map { it.split(CHAR_SPACE) }.map(::hunkBHeader)
    val hunkLinesList = unifiedDiffHeaders.mapIndexed { index, unifiedDiffHeader ->
      val unifiedDiffHeaderIndex = lines.indexOf(unifiedDiffHeader)
      val nextUnifiedDiffHeaderIndex = if (index == unifiedDiffHeaders.lastIndex) {
        lines.lastIndex
      } else {
        lines.indexOf(unifiedDiffHeaders[index + 1]) - 1
      }
      lines.subList(unifiedDiffHeaderIndex + 1, nextUnifiedDiffHeaderIndex)
    }

    return hunkBHeaders
      .zip(hunkLinesList, ::affectedLinesForB)
      .flatten()
  }

  private fun affectedLinesForB(
    hunkBHeader: HunkHeader,
    hunkLines: List<String>
  ): List<Int> {
    val affectedLineNumbers = mutableListOf<Int>()
    var offset = 0
    hunkLines.foldIndexed(affectedLineNumbers) { index, lineNumbersAccumulator, line ->
      if (line.startsWith(CHAR_MINUS) || line.startsWith(CHAR_FORWARD_SLASH)) {
        offset--
      } else if (line.startsWith(CHAR_PLUS)) {
        lineNumbersAccumulator.add(offset + index + hunkBHeader.startLine)
      }
      lineNumbersAccumulator
    }
    return affectedLineNumbers.toList()
  }

  private fun hunkBHeader(headerParts: List<String>): HunkHeader =
    HunkHeader.from(headerParts[INDEX_PART_HUNK_B])
}
