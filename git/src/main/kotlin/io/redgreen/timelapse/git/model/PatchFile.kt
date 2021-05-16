package io.redgreen.timelapse.git.model

class PatchFile(private val unifiedPatch: String) {
  companion object {
    private const val CHAR_NEWLINE = "\n"
    private const val CHAR_SPACE = " "

    private const val INDEX_UNIFIED_DIFF_HEADER = 2
    private const val INDEX_HUNK_B = 2

    fun from(unifiedPatch: String): PatchFile =
      PatchFile(unifiedPatch)
  }

  fun getAffectedLineNumbers(): List<Int> {
    val lines = unifiedPatch.split(CHAR_NEWLINE)
    val unifiedDiffHeader = lines[INDEX_UNIFIED_DIFF_HEADER]
    val headerParts = unifiedDiffHeader.split(CHAR_SPACE)
    val hunkHeaderB = HunkHeader.from(headerParts[INDEX_HUNK_B])
    return listOf(hunkHeaderB.startLine, hunkHeaderB.extendsTill)
      .distinct()
  }
}

class HunkHeader(val startLine: Int, val extendsTill: Int) {
  companion object {
    private const val COMMA = ","

    fun from(hunkHeaderText: String): HunkHeader {
      val hunkHeaderTextSplit = hunkHeaderText.split(COMMA)
      val startLine = hunkHeaderTextSplit.first().toString().drop(1).toInt()
      val extendsTill = hunkHeaderTextSplit.last().toString().toInt()
      return HunkHeader(startLine, extendsTill)
    }
  }
}
