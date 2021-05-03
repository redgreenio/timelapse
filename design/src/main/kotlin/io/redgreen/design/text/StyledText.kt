package io.redgreen.design.text

data class StyledText(val text: String) {
  fun visit(
    onEnterLine: (lineNumber: Int) -> Unit = { /* no-op */ },
    onExitLine: (lineNumber: Int) -> Unit = { /* no-op */ }
  ) {
    var lineNumber = 1
    onEnterLine(lineNumber)
    text.onEachIndexed { index, char ->
      if (char == '\n') {
        lineNumber++
        onEnterLine(lineNumber)
      }

      if ((index + 1 > text.lastIndex) || (index + 1 != text.lastIndex && text[index + 1] == '\n')) {
        onExitLine(lineNumber)
      }
    }
  }
}
