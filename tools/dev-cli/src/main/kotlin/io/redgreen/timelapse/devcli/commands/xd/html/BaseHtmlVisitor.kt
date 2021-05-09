package io.redgreen.timelapse.devcli.commands.xd.html

import io.redgreen.design.text.LineStyle
import io.redgreen.design.text.StyledTextVisitor
import io.redgreen.design.text.TextStyle
import org.apache.commons.text.StringEscapeUtils
import kotlin.LazyThreadSafetyMode.NONE

class BaseHtmlVisitor(private val title: String) : StyledTextVisitor {
  companion object {
    private const val INDENT = "  "
    private const val NEWLINE = "\n"
    private const val CHAR_SPACE = ' '
    private const val CHAR_TAB = '\t'

    private const val NBSP = "&nbsp;"
    private const val TAB = "$NBSP$NBSP$NBSP$NBSP"

    private const val MARKER_TITLE = "{title}"
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
    get() {
      val rawTableRows = contentBuilder.toString()
      val indentedTableRows = rawTableRows.split(NEWLINE).joinToString(NEWLINE) { "$INDENT$it" }

      return template
        .replace(MARKER_TITLE, title)
        .replace(MARKER_TABLE_ROWS, indentedTableRows)
    }

  override fun onText(text: String) {
    contentBuilder.append(toHtmlFriendly(text))
  }

  override fun onEnterLine(lineNumber: Int) {
    if (lineNumber != 1) {
      contentBuilder.append(NEWLINE)
    }
    contentBuilder
      .append("<tr>")
      .append(NEWLINE)
      .append("""$INDENT<td class="line-number">$lineNumber</td>""")
      .append(NEWLINE)
      .append("""$INDENT<td class="muted">""")
  }

  override fun onExitLine(lineNumber: Int) {
    contentBuilder
      .append("</td>")
      .append(NEWLINE)
      .append("</tr>")
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

  private fun toHtmlFriendly(line: String): String {
    val startSpaceCharsTrimmedLine = line.trimStart(CHAR_SPACE)
    val nStartSpaceChars = line.length - startSpaceCharsTrimmedLine.length

    val htmlEscapedLine = StringEscapeUtils.escapeHtml4(line).trimStart(CHAR_SPACE)
    return (1..nStartSpaceChars)
      .joinToString("") { NBSP } + htmlEscapedLine
      .replace("$CHAR_TAB", TAB)
  }
}
