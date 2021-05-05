package io.redgreen.design.text

import java.util.Optional

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
    val textStyle = if (textStyleOptional.isPresent) {
      textStyleOptional.get()
    } else {
      null
    }

    if (textStyle != null && localIndex == textStyle.charIndexRange.first) {
      if (textBuilder.isNotEmpty()) {
        visitor.onText(textBuilder.toString())
        textBuilder.clear()
      }
      visitor.onBeginStyle(textStyle)
    }

    if (textStyle != null && localIndex == textStyle.charIndexRange.last) {
      textBuilder.append(char)
      visitor.onText(textBuilder.toString())
      textBuilder.clear()
      visitor.onEndStyle(textStyle)
    } else if ((textStyle != null && localIndex !in textStyle.charIndexRange) || char != '\n') {
      textBuilder.append(char)
    }
  }

  private fun getLineStyle(lineNumber: Int): Optional<LineStyle> {
    return Optional.ofNullable(lineStyles.find { lineNumber in it.lineNumberRange })
  }

  private fun getTextStyle(lineNumber: Int, localCharIndex: Int): Optional<TextStyle> {
    return Optional.ofNullable(textStyles.find { lineNumber == it.lineNumber && localCharIndex in it.charIndexRange })
  }
}
