package io.redgreen.design.text

import java.lang.StringBuilder
import java.util.Optional

data class StyledText(val text: String) {
  private val lineStyles = mutableListOf<LineStyle>()
  private var textStyle: TextStyle? = null

  fun visit(visitor: StyledTextVisitor) {
    var lineNumber = 1

    getLineStyle(lineNumber)
      .ifPresentOrElse(
        { visitor.onEnterLine(lineNumber, it) },
        { visitor.onEnterLine(lineNumber) }
      )

    val textBuilder = StringBuilder()

    text.onEachIndexed { index, char ->
      if (char == '\n') {
        lineNumber++

        getLineStyle(lineNumber)
          .ifPresentOrElse(
            { visitor.onEnterLine(lineNumber, it) },
            { visitor.onEnterLine(lineNumber) }
          )
      } else {
        if (textStyle != null && index == textStyle!!.charRange.first) {
          visitor.onText(textBuilder.toString())
          textBuilder.clear()
          textBuilder.append(char)
        } else if (textStyle != null && index == textStyle!!.charRange.last) {
          textBuilder.append(char)
          visitor.onText(textBuilder.toString(), textStyle!!)
          textBuilder.clear()
        } else if ((textStyle != null && index !in textStyle!!.charRange) || char != '\n') {
          textBuilder.append(char)
        }
      }

      if ((index + 1 > text.lastIndex) || (index + 1 != text.lastIndex && text[index + 1] == '\n')) {
        visitor.onText(textBuilder.toString())
        textBuilder.clear()

        getLineStyle(lineNumber)
          .ifPresentOrElse(
            { visitor.onExitLine(lineNumber, it) },
            { visitor.onExitLine(lineNumber) }
          )
      }
    }
  }

  private fun getLineStyle(lineNumber: Int): Optional<LineStyle> {
    return Optional.ofNullable(lineStyles.find { lineNumber in it.lineNumberRange })
  }

  fun addStyle(lineStyle: LineStyle): StyledText {
    lineStyles.add(lineStyle)
    return this
  }

  fun addStyle(textStyle: TextStyle): StyledText {
    this.textStyle = textStyle
    return this
  }
}
