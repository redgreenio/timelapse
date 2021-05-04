package io.redgreen.design.text

interface StyledTextVisitor {
  fun onEnterLine(lineNumber: Int)
  fun onExitLine(lineNumber: Int)
  fun onEnterLine(lineNumber: Int, style: LineStyle)
}
