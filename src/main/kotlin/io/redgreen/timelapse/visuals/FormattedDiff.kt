package io.redgreen.timelapse.visuals

import io.redgreen.timelapse.visuals.DiffLine.ContentsEmpty
import io.redgreen.timelapse.visuals.DiffLine.Deletion
import io.redgreen.timelapse.visuals.DiffLine.Insertion
import io.redgreen.timelapse.visuals.DiffLine.Marker
import io.redgreen.timelapse.visuals.DiffLine.Unmodified

class FormattedDiff private constructor(val lines: List<DiffLine>) {
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

      val rawDiffLinesWithoutHeader = rawDiffLines
        .drop(headerLineCount)

      val contentsEmpty = rawDiffLinesWithoutHeader.size == 1 && rawDiffLinesWithoutHeader.first().isEmpty()

      return if (contentsEmpty) {
        FormattedDiff(listOf(ContentsEmpty))
      } else {
        val marker = Marker(rawDiffLinesWithoutHeader.first())

        val diffLines = mutableListOf<DiffLine>()
        var oldLineNumber = marker.oldLineNumber - 1 /* Because the first line is a marker, hence offsetting by 1 */
        var newLineNumber = marker.newLineNumber - 1 /* Because the first line is a marker, hence offsetting by 1 */
        for (line in rawDiffLinesWithoutHeader) {
          val diffLine = toDiffLine(line, oldLineNumber, newLineNumber)
          diffLines.add(diffLine)
          if (diffLine !is Insertion) {
            oldLineNumber++
          }
          if (diffLine !is Deletion) {
            newLineNumber++
          }
          if (diffLine is Marker) {
            oldLineNumber = diffLine.oldLineNumber
            newLineNumber = diffLine.newLineNumber
          }
        }

        FormattedDiff(diffLines.toList())
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

    private fun toDiffLine(
      diffLine: String,
      oldLineNumber: Int,
      newLineNumber: Int
    ): DiffLine {
      return when {
        diffLine.startsWith("@@") && diffLine.endsWith("@@") -> Marker(diffLine)
        diffLine.startsWith('+') -> Insertion(diffLine.safelyTrimFirstSpaceChar(), newLineNumber)
        diffLine.startsWith('-') -> Deletion(diffLine.safelyTrimFirstSpaceChar(), oldLineNumber)
        else -> Unmodified(diffLine.safelyTrimFirstSpaceChar(), oldLineNumber, newLineNumber)
      }
    }

    private fun String.safelyTrimFirstSpaceChar(): String =
      if (this.isNotEmpty()) this.substring(1) else this
  }
}
