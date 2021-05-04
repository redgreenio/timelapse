package io.redgreen.design.text

open class DefaultStyledTextVisitor : StyledTextVisitor {
  override fun onEnterLine(lineNumber: Int) {
    /* override if necessary :) */
  }

  override fun onExitLine(lineNumber: Int) {
    /* override if necessary :) */
  }

  override fun onEnterLine(lineNumber: Int, style: LineStyle) {
    /* override if necessary :) */
  }
}
