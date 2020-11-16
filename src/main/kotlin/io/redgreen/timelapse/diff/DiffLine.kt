package io.redgreen.timelapse.diff

import io.redgreen.timelapse.foo.fastLazy

sealed class DiffLine {
  data class Unmodified(
    val text: String,
    val oldLineNumber: Int,
    val newLineNumber: Int
  ) : DiffLine()

  companion object {
    private const val NON_EXISTENT_LINE_NUMBER = 0
  }

  data class Deletion(
    val text: String,
    val oldLineNumber: Int
  ) : DiffLine()

  data class Insertion(
    val text: String,
    val newLineNumber: Int = NON_EXISTENT_LINE_NUMBER // TODO: 12-11-2020 Remove default parameter
  ) : DiffLine()

  object ContentsEmpty : DiffLine() {
    override fun toString(): String =
      ContentsEmpty::class.java.simpleName
  }

  data class Marker(val text: String) : DiffLine() {
    val oldLineNumber by fastLazy {
      val (oldLineNumberPart, _) = text.drop(4).dropLast(3).split(' ')

      val endIndex = if (oldLineNumberPart.contains(',')) {
        oldLineNumberPart.indexOf(',')
      } else {
        oldLineNumberPart.length
      }
      oldLineNumberPart.substring(0, endIndex).toInt()
    }

    val newLineNumber by fastLazy {
      val (_, newLineNumberPart) = text.drop(4).dropLast(3).split(' ')
      val endIndex = if (newLineNumberPart.contains(',')) {
        newLineNumberPart.indexOf(',')
      } else {
        newLineNumberPart.length
      }
      newLineNumberPart.substring(1, endIndex).toInt()
    }
  }
}
