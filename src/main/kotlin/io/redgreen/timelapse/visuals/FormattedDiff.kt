package io.redgreen.timelapse.visuals

import io.redgreen.timelapse.visuals.DiffSpan.Blank
import io.redgreen.timelapse.visuals.DiffSpan.ContentsEmpty
import io.redgreen.timelapse.visuals.DiffSpan.Deletion
import io.redgreen.timelapse.visuals.DiffSpan.Insertion
import io.redgreen.timelapse.visuals.DiffSpan.Marker
import io.redgreen.timelapse.visuals.DiffSpan.Unmodified

private const val DIFF_TYPE_DELETED = "deleted"
private const val DIFF_TYPE_NEW_FILE = "new file"

class FormattedDiff private constructor(val spans: List<DiffSpan>) {
  companion object {
    private val contentsEmpty = listOf(Unmodified(""))

    fun from(rawDiff: String): FormattedDiff {
      val diffTypeLine = rawDiff.lines().take(2).last()
      val newOrDeletedFile = with(diffTypeLine) { startsWith(DIFF_TYPE_NEW_FILE) || startsWith(DIFF_TYPE_DELETED) }
      val skipLines = if (newOrDeletedFile) { 4 } else { 3 }

      val spans = rawDiff
        .lines()
        .filterIndexed { index, _ -> index > skipLines }
        .map(::toSpan)

      return if (spans == contentsEmpty) {
        FormattedDiff(listOf(ContentsEmpty))
      } else {
        FormattedDiff(spans)
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
