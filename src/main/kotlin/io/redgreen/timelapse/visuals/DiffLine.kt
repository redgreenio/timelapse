package io.redgreen.timelapse.visuals

import java.awt.Color
import kotlin.LazyThreadSafetyMode.NONE

sealed class DiffLine {
  data class Unmodified(
    val text: String,
    val oldLineNumber: Int = NON_EXISTENT,
    val newLineNumber: Int = NON_EXISTENT
  ) : DiffLine()

  data class Deletion(
    val text: String,
    val oldLineNumber: Int
  ) : DiffLine()

  data class Insertion(
    val text: String,
    val newLineNumber: Int = NON_EXISTENT // TODO: 12-11-2020 Remove default parameter
  ) : DiffLine()

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
      val (oldLineNumberPart, _) = text.drop(4).dropLast(3).split(' ')

      val endIndex = if (oldLineNumberPart.contains(',')) {
        oldLineNumberPart.indexOf(',')
      } else {
        oldLineNumberPart.length
      }
      oldLineNumberPart.substring(0, endIndex).toInt()
    }

    val newLineNumber by lazy(NONE) {
      val (_, newLineNumberPart)= text.drop(4).dropLast(3).split(' ')
      val endIndex = if (newLineNumberPart.contains(',')) {
        newLineNumberPart.indexOf(',')
      } else {
        newLineNumberPart.length
      }
      newLineNumberPart.substring(1, endIndex).toInt()
    }
  }

  // FIXME: 12-11-2020 Get rid of this companion object, it should not contain presentation information!
  companion object {
    private const val NON_EXISTENT = 0
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
