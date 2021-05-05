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
  private val lineStyles = mutableListOf<LineStyle>()
  private val textStyles = mutableListOf<TextStyle>()

  fun visit(visitor: StyledTextVisitor) {
    var lineNumber = 1

    getLineStyle(lineNumber)
      .ifPresentOrElse(
        { visitor.onEnterLine(lineNumber, it) },
        { visitor.onEnterLine(lineNumber) }
      )

    val textBuilder = StringBuilder()

    var lineOffset = 0
    text.onEachIndexed { index, char ->
      if (char == '\n') {
        lineOffset = -index - 1
        lineNumber++

        getLineStyle(lineNumber)
          .ifPresentOrElse(
            { visitor.onEnterLine(lineNumber, it) },
            { visitor.onEnterLine(lineNumber) }
          )
      } else {
        val localIndex = index + lineOffset
        processStyledText(lineNumber, localIndex, char, textBuilder, visitor)
      }

      if ((index + 1 > text.lastIndex) || (index + 1 != text.lastIndex && text[index + 1] == '\n')) {
        if (textBuilder.isNotEmpty()) {
          visitor.onText(textBuilder.toString())
          textBuilder.clear()
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
    lineNumber: Int,
    localIndex: Int,
    char: Char,
    textBuilder: StringBuilder,
    visitor: StyledTextVisitor
  ) {
    val textStyleOptional = getTextStyle(lineNumber, localIndex)
    val textStylePair = if (textStyleOptional.isPresent) {
      textStyleOptional.get()
    } else {
      null
    }
    val textStyles = textStylePair?.first
    val styleOccurrence = textStylePair?.second

    if (textStyles != null && (styleOccurrence == BEGIN || styleOccurrence == BEGIN_END)) {
      if (textBuilder.isNotEmpty()) {
        visitor.onText(textBuilder.toString())
        textBuilder.clear()
      }
      textStyles.onEach(visitor::onBeginStyle)
    }

    if (textStyles != null && (styleOccurrence == END || styleOccurrence == BEGIN_END)) {
      textBuilder.append(char)
      visitor.onText(textBuilder.toString())
      textBuilder.clear()
      visitor.onEndStyle(textStyles.first())
    } else if ((textStyles != null && localIndex !in textStyles.first().charIndexRange) || char != '\n') {
      textBuilder.append(char)
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
