package io.redgreen.design.text

import io.redgreen.design.text.StyleOccurrence.BEGIN
import io.redgreen.design.text.StyleOccurrence.BEGIN_END
import io.redgreen.design.text.StyleOccurrence.END
import java.util.Optional

private enum class StyleOccurrence {
  BEGIN,
  END,
  BEGIN_END,
}

data class StyledText(val text: String) {
  companion object {
    private const val CHAR_NEWLINE = '\n'
  }

  private val lineStyles = mutableListOf<LineStyle>()
  private val textStyles = mutableListOf<TextStyle>()

  fun visit(visitor: StyledTextVisitor) {
    var lineNumber = 1

    getLineStyle(lineNumber)
      .ifPresentOrElse(
        { visitor.onEnterLine(lineNumber, it) },
        { visitor.onEnterLine(lineNumber) }
      )

    val buffer = StringBuilder()

    var lineOffset = 0
    text.onEachIndexed { index, char ->
      if (char == CHAR_NEWLINE) {
        lineOffset = -index - 1
        lineNumber++

        getLineStyle(lineNumber)
          .ifPresentOrElse(
            { visitor.onEnterLine(lineNumber, it) },
            { visitor.onEnterLine(lineNumber) }
          )
      } else {
        val localIndex = index + lineOffset
        val textStyleOptional = getTextStyle(lineNumber, localIndex)
        if (textStyleOptional.isPresent) {
          processStyledText(textStyleOptional.get(), localIndex, char, buffer, visitor)
        } else {
          buffer.append(char)
        }
      }

      val endOfContent = index + 1 > text.lastIndex
      val nextCharIsNewline = !endOfContent && index + 1 <= text.lastIndex && text[index + 1] == CHAR_NEWLINE
      if (endOfContent || nextCharIsNewline) {
        if (buffer.isNotEmpty()) {
          visitor.onText(buffer.toString())
          buffer.clear()
        }

        getLineStyle(lineNumber)
          .ifPresentOrElse(
            { visitor.onExitLine(lineNumber, it) },
            { visitor.onExitLine(lineNumber) }
          )
      }
    }
  }

  fun addStyle(lineStyle: LineStyle): StyledText {
    lineStyles.add(lineStyle)
    return this
  }

  fun addStyle(textStyle: TextStyle): StyledText {
    textStyles.add(textStyle)
    return this
  }

  private fun processStyledText(
    textStylePair: Pair<List<TextStyle>, StyleOccurrence>,
    localIndex: Int,
    char: Char,
    buffer: StringBuilder,
    visitor: StyledTextVisitor
  ) {
    val (textStyles, styleOccurrence) = textStylePair

    if (styleOccurrence == BEGIN || styleOccurrence == BEGIN_END) {
      if (buffer.isNotEmpty()) {
        visitor.onText(buffer.toString())
        buffer.clear()
      }
      textStyles.onEach(visitor::onBeginStyle)
    }

    if (styleOccurrence == END || styleOccurrence == BEGIN_END) {
      buffer.append(char)
      visitor.onText(buffer.toString())
      buffer.clear()
      textStyles.reversed().onEach(visitor::onEndStyle)
    } else if ((localIndex !in textStyles.first().charIndexRange) || char != CHAR_NEWLINE) {
      buffer.append(char)
    }
  }

  private fun getLineStyle(lineNumber: Int): Optional<LineStyle> {
    return Optional.ofNullable(lineStyles.find { lineNumber in it.lineNumberRange })
  }

  private fun getTextStyle(lineNumber: Int, localCharIndex: Int): Optional<Pair<List<TextStyle>, StyleOccurrence>> {
    val beginEndTextStylePredicate = beginEndTextStylePredicate(lineNumber, localCharIndex)
    val beginTextStylePredicate = beginStylePredicate(lineNumber, localCharIndex)
    val endStylePredicate = endStylePredicate(lineNumber, localCharIndex)

    val predicatesToOccurrences = listOf(
      beginEndTextStylePredicate to BEGIN_END,
      beginTextStylePredicate to BEGIN,
      endStylePredicate to END,
    )

    for ((predicate, occurrence) in predicatesToOccurrences) {
      val textStyles = textStyles.filter(predicate)
      if (textStyles.isNotEmpty()) {
        return Optional.of(textStyles to occurrence)
      }
    }

    return Optional.empty()
  }

  private fun beginEndTextStylePredicate(
    lineNumber: Int,
    localCharIndex: Int
  ): (TextStyle) -> Boolean {
    return { textStyle ->
      lineNumber == textStyle.lineNumber &&
        localCharIndex == textStyle.charIndexRange.first &&
        localCharIndex == textStyle.charIndexRange.last
    }
  }

  private fun beginStylePredicate(
    lineNumber: Int,
    localCharIndex: Int
  ): (TextStyle) -> Boolean {
    return { textStyle ->
      lineNumber == textStyle.lineNumber && localCharIndex == textStyle.charIndexRange.first
    }
  }

  private fun endStylePredicate(
    lineNumber: Int,
    localCharIndex: Int
  ): (TextStyle) -> Boolean {
    return { textStyle ->
      lineNumber == textStyle.lineNumber && localCharIndex == textStyle.charIndexRange.last
    }
  }
}
