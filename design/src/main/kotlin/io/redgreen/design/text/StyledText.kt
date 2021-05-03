package io.redgreen.design.text

data class StyledText(val text: String) {
  fun visit(onEnterNewline: (lineNumber: Int) -> Unit) {
    var lineNumber = 1
    onEnterNewline(lineNumber)
    text.onEach {
      if (it == '\n') {
        lineNumber++
        onEnterNewline(lineNumber)
      }
    }
  }
}
