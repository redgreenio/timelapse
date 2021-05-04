package io.redgreen.design.text

data class StyledText(val text: String) {
  private var lineStyle: LineStyle? = null

  fun visit(visitor: StyledTextVisitor) {
    var lineNumber = 1
    if (lineStyle != null && lineNumber in lineStyle!!.lineNumberRange) {
      visitor.onEnterLine(lineNumber, lineStyle!!)
    } else {
      visitor.onEnterLine(lineNumber)
    }
    text.onEachIndexed { index, char ->
      if (char == '\n') {
        lineNumber++
        if (lineStyle != null && lineNumber in lineStyle!!.lineNumberRange) {
          visitor.onEnterLine(lineNumber, lineStyle!!)
        } else {
          visitor.onEnterLine(lineNumber)
        }
      }

      if ((index + 1 > text.lastIndex) || (index + 1 != text.lastIndex && text[index + 1] == '\n')) {
        visitor.onExitLine(lineNumber)
      }
    }
  }

  fun addStyle(lineStyle: LineStyle) {
    this.lineStyle = lineStyle
  }
}
