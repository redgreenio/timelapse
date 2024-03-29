package io.redgreen.timelapse.diff

import io.redgreen.timelapse.foo.fastLazy

sealed class DiffLine {
  companion object {
    private const val NON_EXISTENT_LINE_NUMBER = 0
  }

  data class Unmodified(
    val text: String,
    val oldLineNumber: Int,
    val newLineNumber: Int
  ) : DiffLine()

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

  sealed class Marker(open val text: String) : DiffLine() {
    data class Text(override val text: String) : Marker(text) {
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

    data class Binary(override val text: String) : Marker(text)
  }

  data class FileModeChanged(
    val oldMode: Int,
    val newMode: Int
  ) : DiffLine()
}
