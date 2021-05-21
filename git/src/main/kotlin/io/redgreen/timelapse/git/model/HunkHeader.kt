package io.redgreen.timelapse.git.model

import kotlin.math.abs

internal data class HunkHeader(val startLine: Int, val extendsTill: Int) {
  companion object {
    private const val COMMA = ","

    fun from(hunkHeaderText: String): HunkHeader {
      val hunkHeaderTextSplit = hunkHeaderText.split(COMMA)
      val startLine = hunkHeaderTextSplit.first().toString().drop(1).toInt()
      val extendsTill = startLine + abs(hunkHeaderTextSplit.last().toString().toInt()) - 1
      return HunkHeader(startLine, extendsTill)
    }
  }
}
