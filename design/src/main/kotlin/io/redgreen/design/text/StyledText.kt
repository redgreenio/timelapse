package io.redgreen.design.text

data class StyledText(val text: String) {
  fun visit(visitor: StyledTextVisitor) {
    var lineNumber = 1
    visitor.onEnterLine(lineNumber)
    text.onEachIndexed { index, char ->
      if (char == '\n') {
        lineNumber++
        visitor.onEnterLine(lineNumber)
      }

      if ((index + 1 > text.lastIndex) || (index + 1 != text.lastIndex && text[index + 1] == '\n')) {
        visitor.onExitLine(lineNumber)
      }
    }
  }
}
