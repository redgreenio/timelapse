package io.redgreen.timelapse.devcli.commands.xd.html.visitors

import io.redgreen.design.text.LineStyle
import io.redgreen.design.text.StyledTextVisitor
import io.redgreen.design.text.TextStyle
import io.redgreen.timelapse.devcli.commands.TOOL_VERSION
import org.apache.commons.text.StringEscapeUtils
import kotlin.LazyThreadSafetyMode.NONE

@SuppressWarnings("TooManyFunctions")
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
  private var isInsideMultilineString = false

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

    addLineNumberRow(lineNumber)
    muteOrUnmuteLine(lineNumber)
  }

  override fun onEnterLine(lineNumber: Int, lineStyle: LineStyle) {
    if (lineNumber != 1) {
      contentBuilder.append(NEWLINE)
    }

    if (isInsideMultilineString) {
      contentBuilder.append("<span class=\"string\">")
    }

    if (lineStyle.name == "begin-function") {
      contentBuilder.append("<tbody>").append("\n")
    }

    addLineNumberRow(lineNumber)
    muteOrUnmuteLine(lineNumber)
  }

  override fun onExitLine(lineNumber: Int) {
    closeTags()
  }

  override fun onExitLine(lineNumber: Int, lineStyle: LineStyle) {
    closeTags()

    if (lineStyle.name == "end-function") {
      contentBuilder
        .append(NEWLINE)
        .append("</tbody>")
    }
  }

  override fun onBeginStyle(textStyle: TextStyle) {
    if (textStyle.name == "open-multiline-string") {
      isInsideMultilineString = true
    }

    if (textStyle.name == "close-multiline-string") {
      isInsideMultilineString = false
    }

    if (textStyle.name == "identifier") {
      val dataIdentifier = textStyle.extra.get()
      contentBuilder.append("""<span class="${textStyle.name}" data-identifier="$dataIdentifier">""")
    } else if (textStyle.name != "end-string") {
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

  private fun addLineNumberRow(lineNumber: Int) {
    contentBuilder
      .append("<tr>")
      .append(NEWLINE)
      .append("""$INDENT<td class="line-number">$lineNumber</td>""")
      .append(NEWLINE)
  }

  private fun muteOrUnmuteLine(lineNumber: Int) {
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

  private fun closeTags() {
    if (isInsideMultilineString) {
      contentBuilder.append("</span>")
    }

    contentBuilder
      .append("</td>")
      .append(NEWLINE)
      .append("</tr>")
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
