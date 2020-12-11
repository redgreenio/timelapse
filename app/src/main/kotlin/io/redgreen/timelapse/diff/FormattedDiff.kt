package io.redgreen.timelapse.diff

import io.redgreen.timelapse.diff.DiffLine.ContentsEmpty
import io.redgreen.timelapse.diff.DiffLine.Deletion
import io.redgreen.timelapse.diff.DiffLine.FileModeChanged
import io.redgreen.timelapse.diff.DiffLine.Insertion
import io.redgreen.timelapse.diff.DiffLine.Marker
import io.redgreen.timelapse.diff.DiffLine.Unmodified

class FormattedDiff internal constructor(val lines: List<DiffLine>) {
  companion object {
    private const val DIFF_MODE_LINE_INDEX = 1

    private const val DIFF_MODE_NEW_FILE = "new file mode"
    private const val DIFF_MODE_DELETED = "deleted file mode"

    private const val DIFF_FILE_MODE_CHANGED = "old mode"
    private const val DIFF_OLD_MODE_LINE_INDEX = 1
    private const val DIFF_NEW_MODE_LINE_INDEX = 2

    private const val HEADER_LINES_COUNT_NEW_OR_DELETED_FILE = 5
    private const val HEADER_LINES_COUNT_MODIFIED_FILE = 4

    fun from(rawDiff: String): FormattedDiff {
      val rawDiffLines = rawDiff.lines()
      val diffModeLine = rawDiffLines[DIFF_MODE_LINE_INDEX]
      val headerLineCount = headerLineCount(diffModeLine)

      val rawDiffLinesWithoutHeader = rawDiffLines
        .drop(headerLineCount)

      @Suppress("ReplaceSizeZeroCheckWithIsEmpty")
      // 'isEmpty(): Boolean' is deprecated. This member is not fully supported by Kotlin compiler, so it may be
      // absent or have different signature in next major version (JDK 15)
      val contentsEmpty = rawDiffLinesWithoutHeader.size == 1 && rawDiffLinesWithoutHeader.first().length == 0

      return if (contentsEmpty) {
        FormattedDiff(listOf(ContentsEmpty))
      } else if (diffModeLine.startsWith(DIFF_FILE_MODE_CHANGED)) {
        val oldModeLine = rawDiffLines[DIFF_OLD_MODE_LINE_INDEX]
        val oldMode = oldModeLine.substring(oldModeLine.lastIndexOf(' ') + 1).toInt()
        val newModeLine = rawDiffLines[DIFF_NEW_MODE_LINE_INDEX]
        val newMode = newModeLine.substring(newModeLine.lastIndexOf(' ') + 1).toInt()
        FormattedDiff(listOf(FileModeChanged(oldMode, newMode)))
      } else {
        createFormattedDiff(rawDiffLinesWithoutHeader)
      }
    }

    private fun headerLineCount(diffModeLine: String): Int {
      val newOrDeletedFile = diffModeLine.startsWith(DIFF_MODE_NEW_FILE) ||
          diffModeLine.startsWith(DIFF_MODE_DELETED)
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
