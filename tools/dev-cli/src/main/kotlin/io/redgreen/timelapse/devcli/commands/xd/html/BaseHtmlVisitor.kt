package io.redgreen.timelapse.devcli.commands.xd.html

import io.redgreen.design.text.LineStyle
import io.redgreen.design.text.StyledTextVisitor
import io.redgreen.design.text.TextStyle
import kotlin.LazyThreadSafetyMode.NONE

class BaseHtmlVisitor : StyledTextVisitor {
  companion object {
    private const val INDENT = "  "
    private const val NEWLINE = "\n"

    private const val BEGIN_TR = "<tr>"
    private const val END_TR = "</tr>"
    private const val BEGIN_TD = "<td>"
    private const val END_TD = "</td>"

    private const val MARKER_TABLE_ROWS = "{table-rows}"
  }

  private val contentBuilder = StringBuilder()

  private val template by lazy(NONE) {
    BaseHtmlVisitor::class.java.classLoader
      .getResourceAsStream("template.html")!!
      .reader()
      .readText()
  }

  val content: String
    get() = template.replace(MARKER_TABLE_ROWS, contentBuilder.toString())

  override fun onText(text: String) {
    contentBuilder.append(text)
  }

  override fun onEnterLine(lineNumber: Int) {
    if (lineNumber != 1) {
      contentBuilder.append(NEWLINE)
    }
    contentBuilder
      .append(BEGIN_TR)
      .append(NEWLINE)
      .append("$INDENT$BEGIN_TD$lineNumber$END_TD")
      .append(NEWLINE)
      .append("$INDENT$BEGIN_TD")
  }

  override fun onExitLine(lineNumber: Int) {
    contentBuilder
      .append(END_TD)
      .append(NEWLINE)
      .append(END_TR)
  }

  override fun onEnterLine(lineNumber: Int, lineStyle: LineStyle) {
    // no-p
  }

  override fun onExitLine(lineNumber: Int, lineStyle: LineStyle) {
    // no-p
  }

  override fun onBeginStyle(textStyle: TextStyle) {
    // no-p
  }

  override fun onEndStyle(textStyle: TextStyle) {
    // no-p
  }
}
