package io.redgreen.timelapse.devcli.commands.xd.html.visitors

import io.redgreen.design.text.LineStyle
import io.redgreen.design.text.StyledTextVisitor
import io.redgreen.design.text.TextStyle
import io.redgreen.timelapse.devcli.commands.TOOL_VERSION
import org.apache.commons.text.StringEscapeUtils
import kotlin.LazyThreadSafetyMode.NONE

class BaseHtmlVisitor(
  private val title: String,
  private val affectedLineNumbers: List<Int> = emptyList()
) : StyledTextVisitor {
  companion object {
    private const val INDENT = "  "
    private const val NEWLINE = "\n"
    private const val CHAR_SPACE = ' '
    private const val CHAR_TAB = '\t'

    private const val NBSP = "&nbsp;"
    private const val TAB = "$NBSP$NBSP$NBSP$NBSP"

    private const val MARKER_TITLE = "{title}"
    private const val MARKER_TABLE_ROWS = "{table-rows}"
    private const val MARKER_VERSION = "{tool-version}"
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
        .replace(MARKER_VERSION, TOOL_VERSION)
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

    val muted = lineNumber !in affectedLineNumbers
    if (muted) {
      contentBuilder.append("""$INDENT<td class="muted">""")
    } else {
      contentBuilder.append("""$INDENT<td>""")
      if (isInsideMultilineString) {
        contentBuilder.append("<span class=\"string\">")
      }
    }
  }

  override fun onExitLine(lineNumber: Int) {
    if (isInsideMultilineString) {
      contentBuilder.append("</span>")
    }

    contentBuilder
      .append("</td>")
      .append(NEWLINE)
      .append("</tr>")
  }

  override fun onEnterLine(lineNumber: Int, lineStyle: LineStyle) {
    if (isInsideMultilineString) {
      contentBuilder.append("<span class=\"string\">")
    }
  }

  override fun onExitLine(lineNumber: Int, lineStyle: LineStyle) {
    if (isInsideMultilineString) {
      contentBuilder.append("</span>")
    }
  }

  private var isInsideMultilineString = false

  override fun onBeginStyle(textStyle: TextStyle) {
    if (textStyle.name == "open-multiline-string") {
      isInsideMultilineString = true
    }

    if (textStyle.name == "close-multiline-string") {
      isInsideMultilineString = false
    }

    if (textStyle.name != "end-string") {
      contentBuilder.append("""<span class="${textStyle.name}">""")
    }
  }

  override fun onEndStyle(textStyle: TextStyle) {
    if (textStyle.name != "string") {
      contentBuilder.append("</span>")
    }

    if (textStyle.name == "open-multiline-string" && isInsideMultilineString) {
      contentBuilder.append("<span class=\"string\">")
    } else if (textStyle.name == "close-multiline-string") {
      contentBuilder.append("</span>")
    }
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
