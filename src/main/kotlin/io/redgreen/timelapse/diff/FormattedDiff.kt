package io.redgreen.timelapse.diff

import io.redgreen.timelapse.diff.DiffLine.ContentsEmpty
import io.redgreen.timelapse.diff.DiffLine.Deletion
import io.redgreen.timelapse.diff.DiffLine.Insertion
import io.redgreen.timelapse.diff.DiffLine.Marker
import io.redgreen.timelapse.diff.DiffLine.Unmodified

class FormattedDiff internal constructor(val lines: List<DiffLine>) {
  companion object {
    private const val DIFF_TYPE_LINE_INDEX = 1
    private const val DIFF_TYPE_NEW_FILE = "new file"
    private const val DIFF_TYPE_DELETED = "deleted"

    private const val HEADER_LINES_COUNT_NEW_OR_DELETED_FILE = 5
    private const val HEADER_LINES_COUNT_MODIFIED_FILE = 4

    fun from(rawDiff: String): FormattedDiff {
      val rawDiffLines = rawDiff.lines()
      val headerLineCount = headerLineCount(rawDiffLines[DIFF_TYPE_LINE_INDEX])

      val rawDiffLinesWithoutHeader = rawDiffLines
        .drop(headerLineCount)

      val contentsEmpty = rawDiffLinesWithoutHeader.size == 1 && rawDiffLinesWithoutHeader.first().isEmpty()

      return if (contentsEmpty) {
        FormattedDiff(listOf(ContentsEmpty))
      } else {
        createFormattedDiff(rawDiffLinesWithoutHeader)
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

    private fun createFormattedDiff(
      rawDiffLinesWithoutHeader: List<String>
    ): FormattedDiff {
      val line = rawDiffLinesWithoutHeader.first()
      if (line == "Binary files differ") {
        return FormattedDiff(listOf(Marker.Binary(line)))
      }

      val marker = Marker.Text(line)
      var oldLineNumber = marker.oldLineNumber - 1 /* ^ the first line is a marker, hence offsetting by 1 */
      var newLineNumber = marker.newLineNumber - 1

      val diffLines = mutableListOf<DiffLine>()
      for (rawDiffLine in rawDiffLinesWithoutHeader) {
        val diffLine = toDiffLine(rawDiffLine, oldLineNumber, newLineNumber)
        diffLines.add(diffLine)
        if (diffLine !is Insertion) {
          oldLineNumber++
        }
        if (diffLine !is Deletion) {
          newLineNumber++
        }
        if (diffLine is Marker.Text) {
          oldLineNumber = diffLine.oldLineNumber
          newLineNumber = diffLine.newLineNumber
        }
      }

      return FormattedDiff(diffLines.toList())
    }

    private fun toDiffLine(
      diffLine: String,
      oldLineNumber: Int,
      newLineNumber: Int
    ): DiffLine {
      return when {
        diffLine.startsWith("@@") && diffLine.endsWith("@@") -> Marker.Text(diffLine)
        diffLine.startsWith('+') -> Insertion(diffLine.safelyTrimFirstSpaceChar(), newLineNumber)
        diffLine.startsWith('-') -> Deletion(diffLine.safelyTrimFirstSpaceChar(), oldLineNumber)
        else -> Unmodified(diffLine.safelyTrimFirstSpaceChar(), oldLineNumber, newLineNumber)
      }
    }

    private fun String.safelyTrimFirstSpaceChar(): String =
      if (this.isNotEmpty()) this.substring(1) else this
  }
}
