package io.redgreen.timelapse.visuals

import io.redgreen.timelapse.visuals.DiffSpan.Blank
import io.redgreen.timelapse.visuals.DiffSpan.ContentsEmpty
import io.redgreen.timelapse.visuals.DiffSpan.Deletion
import io.redgreen.timelapse.visuals.DiffSpan.Insertion
import io.redgreen.timelapse.visuals.DiffSpan.Marker
import io.redgreen.timelapse.visuals.DiffSpan.Unmodified

class FormattedDiff private constructor(val spans: List<DiffSpan>) {
  companion object {
    private const val DIFF_TYPE_LINE_INDEX = 1
    private const val DIFF_TYPE_NEW_FILE = "new file"
    private const val DIFF_TYPE_DELETED = "deleted"

    private const val HEADER_LINES_COUNT_NEW_OR_DELETED_FILE = 5
    private const val HEADER_LINES_COUNT_MODIFIED_FILE = 4

    private const val EMPTY_STRING = ""

    private val contentsEmpty = listOf(Unmodified(EMPTY_STRING))

    fun from(rawDiff: String): FormattedDiff {
      val rawDiffLines = rawDiff.lines()
      val headerLineCount = headerLineCount(rawDiffLines[DIFF_TYPE_LINE_INDEX])

      val spans = rawDiffLines
        .drop(headerLineCount)
        .map(::toSpan)

      return if (spans == contentsEmpty) {
        FormattedDiff(listOf(ContentsEmpty))
      } else {
        FormattedDiff(spans)
      }
    }

    private fun headerLineCount(diffTypeLine: String): Int {
      val newOrDeletedFile = diffTypeLine.startsWith(DIFF_TYPE_NEW_FILE) ||
          diffTypeLine.startsWith(DIFF_TYPE_DELETED)
      return if (newOrDeletedFile) {
        HEADER_LINES_COUNT_NEW_OR_DELETED_FILE
      } else {
        HEADER_LINES_COUNT_MODIFIED_FILE
      }
    }

    private fun toSpan(diffLine: String): DiffSpan {
      return when {
        diffLine.length == 1 && diffLine.isBlank() -> Blank
        diffLine.startsWith("@@") && diffLine.endsWith("@@") -> Marker(diffLine)
        diffLine.startsWith('+') -> Insertion(diffLine.safelyTrimFirstSpaceChar())
        diffLine.startsWith('-') -> Deletion(diffLine.safelyTrimFirstSpaceChar())
        else -> Unmodified(diffLine.safelyTrimFirstSpaceChar())
      }
    }

    private fun String.safelyTrimFirstSpaceChar(): String =
      if (this.isNotEmpty()) this.substring(1) else this
  }
}
