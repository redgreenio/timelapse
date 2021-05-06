package io.redgreen.design.text

interface StyledTextVisitor {
  /**
   * Invoked just before the first character of a line.
   *
   * @param lineNumber line number of the line. Starts with 1.
   */
  fun onEnterLine(lineNumber: Int)

  /**
   * Invoked after the last character of a line, but before the newline character (if one exists).
   *
   * @param lineNumber line number of the line. Starts with 1.
   */
  fun onExitLine(lineNumber: Int)

  /**
   * Invoked just before the first character of a line if [LineStyle] is present.
   * [onEnterLine(Int)] and [onEnterLine(Int, LineStyle)] are mutually exclusive,
   * only one of them will be invoked.
   *
   * @param lineNumber line number of the line. Starts with 1.
   * @param lineStyle [LineStyle] associated with the line.
   */
  fun onEnterLine(lineNumber: Int, lineStyle: LineStyle)

  /**
   * Invoked after the last character of a line, but before the newline character (if one exists).
   * [onExitLine(Int)] and [onExitLine(Int, LineStyle)] are mutually exclusive, only one of them
   * will be invoked.
   *
   * @param lineNumber line number of the line. Starts with 1.
   * @param lineStyle [LineStyle] associated with the line.
   */
  fun onExitLine(lineNumber: Int, lineStyle: LineStyle)

  /**
   * Invoked with the accumulated text before encountering a [onBeginStyle], [onEndStyle], [onExitLine(Int)], or
   * [onExitLine(Int, LineStyle)].
   *
   * @param text the accumulated text.
   */
  fun onText(text: String)

  /**
   * Invoked on encountering the beginning of a [TextStyle].
   *
   * @param textStyle the beginning [TextStyle].
   */
  fun onBeginStyle(textStyle: TextStyle)

  /**
   * Invoked on encountering the end of a [TextStyle].
   *
   * @param textStyle the terminating [TextStyle].
   */
  fun onEndStyle(textStyle: TextStyle)
}
