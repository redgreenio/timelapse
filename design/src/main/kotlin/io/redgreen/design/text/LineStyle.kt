package io.redgreen.design.text

data class LineStyle(
  val name: String,
  val lineNumberRange: IntRange
) {
  constructor(name: String, lineNumber: Int) : this(name, lineNumber..lineNumber)
}
