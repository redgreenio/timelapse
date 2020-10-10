package io.redgreen.timelapse.visuals

import io.redgreen.timelapse.visuals.DiffSpan.Blank
import io.redgreen.timelapse.visuals.DiffSpan.Deletion
import io.redgreen.timelapse.visuals.DiffSpan.Insertion
import io.redgreen.timelapse.visuals.DiffSpan.Unmodified

class FormattedDiff(val spans: List<DiffSpan>) {
  companion object {
    fun from(rawDiff: String): FormattedDiff {
      val spans = rawDiff
        .lines()
        .filterIndexed { index, _ -> index > 4 }
        .map(::toSpan)

      return FormattedDiff(spans)
    }

    private fun toSpan(diffLine: String): DiffSpan {
      return when {
        diffLine.length == 1 && diffLine.isBlank() -> Blank
        diffLine.startsWith('+') -> Insertion(diffLine.safelyTrimFirstSpaceChar())
        diffLine.startsWith('-') -> Deletion(diffLine.safelyTrimFirstSpaceChar())
        else -> Unmodified(diffLine.safelyTrimFirstSpaceChar())
      }
    }

    private fun String.safelyTrimFirstSpaceChar(): String =
      if (this.isNotEmpty()) this.substring(1) else this
  }
}
