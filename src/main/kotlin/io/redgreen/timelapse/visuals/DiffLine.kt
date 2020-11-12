package io.redgreen.timelapse.visuals

import java.awt.Color
import kotlin.LazyThreadSafetyMode.NONE

sealed class DiffLine {
  data class Unmodified(val text: String) : DiffLine()
  data class Deletion(val text: String) : DiffLine()
  data class Insertion(val text: String) : DiffLine()

  object Blank : DiffLine() {
    override fun toString(): String =
      Blank::class.java.simpleName
  }

  object ContentsEmpty : DiffLine() {
    override fun toString(): String =
      ContentsEmpty::class.java.simpleName
  }

  data class Marker(val text: String) : DiffLine() {
    val oldLineNumber by lazy(NONE) {
      val oldLineNumberPart = text.drop(4)
      oldLineNumberPart.substring(0, oldLineNumberPart.indexOf(',')).toInt()
    }

    val newLineNumber by lazy(NONE) {
      val newLineNumberPart = text.drop(4).split(' ').first()
      newLineNumberPart.substring(0, newLineNumberPart.indexOf(',')).toInt()
    }
  }

  companion object {
    private val insertionColor = Color(198, 240, 194)
    private val deletionColor = Color(240, 194, 194)
    private val unmodifiedColor = Color(255, 255, 255)

    private val sectionBlocks = (0..100)
      .fold(StringBuilder(), { builder, _ -> builder.append('▓') })
      .append('\n')
      .toString() 
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
      is Marker -> sectionBlocks
    }
  }
}
