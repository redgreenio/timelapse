package io.redgreen.timelapse.git.model

internal class HunkHeader(val startLine: Int) {
  companion object {
    private const val COMMA = ","

    fun from(hunkHeaderText: String): HunkHeader {
      val hunkHeaderTextSplit = hunkHeaderText.split(COMMA)
      val startLine = hunkHeaderTextSplit.first().toString().drop(1).toInt()
      return HunkHeader(startLine)
    }
  }
}
