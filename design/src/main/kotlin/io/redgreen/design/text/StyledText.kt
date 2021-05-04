package io.redgreen.design.text

data class StyledText(val text: String) {
  private var lineStyle: LineStyle? = null

  fun visit(visitor: StyledTextVisitor) {
    var lineNumber = 1

    if (doesLineHasStyle(lineNumber)) {
      visitor.onEnterLine(lineNumber, lineStyle!!)
    } else {
      visitor.onEnterLine(lineNumber)
    }

    text.onEachIndexed { index, char ->
      if (char == '\n') {
        lineNumber++
        if (doesLineHasStyle(lineNumber)) {
          visitor.onEnterLine(lineNumber, lineStyle!!)
        } else {
          visitor.onEnterLine(lineNumber)
        }
      }

      if ((index + 1 > text.lastIndex) || (index + 1 != text.lastIndex && text[index + 1] == '\n')) {
        if (doesLineHasStyle(lineNumber)) {
          visitor.onExitLine(lineNumber, lineStyle!!)
        } else {
          visitor.onExitLine(lineNumber)
        }
      }
    }
  }

  private fun doesLineHasStyle(lineNumber: Int): Boolean {
    return lineStyle != null && lineNumber in lineStyle!!.lineNumberRange
  }

  fun addStyle(lineStyle: LineStyle) {
    this.lineStyle = lineStyle
  }
}
