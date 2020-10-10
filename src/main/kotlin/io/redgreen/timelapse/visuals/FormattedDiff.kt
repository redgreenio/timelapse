package io.redgreen.timelapse.visuals

import io.redgreen.timelapse.visuals.DiffSpan.Blank
import io.redgreen.timelapse.visuals.DiffSpan.Deletion
import io.redgreen.timelapse.visuals.DiffSpan.Insertion
import io.redgreen.timelapse.visuals.DiffSpan.Text

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
        diffLine.startsWith('+') -> Insertion(diffLine.trimFirstSpaceChar())
        diffLine.startsWith('-') -> Deletion(diffLine.trimFirstSpaceChar())
        else -> Text(diffLine.trimFirstSpaceChar())
      }
    }

    private fun String.trimFirstSpaceChar(): String =
      this.substring(1)
  }
}

sealed class DiffSpan {
  data class Text(val text: String) : DiffSpan()
  data class Deletion(val text: String) : DiffSpan()
  data class Insertion(val text: String) : DiffSpan()
  object Blank : DiffSpan()
}
