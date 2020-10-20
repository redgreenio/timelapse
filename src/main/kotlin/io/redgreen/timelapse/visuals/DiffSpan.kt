package io.redgreen.timelapse.visuals

import java.awt.Color

sealed class DiffSpan {
  data class Unmodified(val text: String) : DiffSpan()
  data class Deletion(val text: String) : DiffSpan()
  data class Insertion(val text: String) : DiffSpan()

  object Blank : DiffSpan() {
    override fun toString(): String =
      Blank::class.java.simpleName
  }

  object ContentsEmpty : DiffSpan() {
    override fun toString(): String =
      ContentsEmpty::class.java.simpleName
  }

  companion object {
    private val insertionColor = Color(198, 240, 194)
    private val deletionColor = Color(240, 194, 194)
    private val unmodifiedColor = Color(255, 255, 255)
  }

  fun backgroundColor(): Color {
    return when (this) {
      is Insertion -> insertionColor
      is Deletion -> deletionColor
      else -> unmodifiedColor
    }
  }

  fun text(): String {
    return when (this) {
      is Unmodified -> "${this.text}\n"
      is Deletion -> "${this.text}\n"
      is Insertion -> "${this.text}\n"
      Blank -> "\n"
      ContentsEmpty -> "<contents empty>"
    }
  }
}
