package io.redgreen.timelapse.diff

import io.redgreen.timelapse.diff.DiffLine.ContentsEmpty
import io.redgreen.timelapse.diff.DiffLine.Deletion
import io.redgreen.timelapse.diff.DiffLine.FileModeChanged
import io.redgreen.timelapse.diff.DiffLine.Insertion
import io.redgreen.timelapse.diff.DiffLine.Marker
import io.redgreen.timelapse.diff.DiffLine.Unmodified
import org.apache.commons.text.StringEscapeUtils

private const val REPLACE_SECTION = "<!-- Replace -->"
private const val ZERO_WIDTH_SPACE = "\u200B"

private const val CHAR_SPACE = ' '
private const val CHAR_TAB = '\t'

private const val LT = "&lt;"
private const val GT = "&gt;"
private const val NBSP = "&nbsp;"
private const val TAB = "$NBSP$NBSP$NBSP$NBSP"

private val template = """
<html>
  <head>
    <style>
      #page {
        padding: 0;
        margin: 0;
      }

      table {
        width: 100%;
        border-collapse: collapse;
      }

      table td {
        font-family: "Fira Code", "JetBrains Mono", "Lucida Console";
        font-size: small;
      }

      table tr td:first-child {
        padding-left: 24px;
      }

      .deletion {
        background-color: #ffeef0;
      }

      .insertion {
        background-color: #e6ffed;
      }

      .blob {
        padding-left: 10px;
      }

      .diff-section {
        background-color: #f1f8ff;
      }

      .line-number {
        width:1%;
        white-space:nowrap;
        text-align: right;
        vertical-align: top;
        padding: 2px 8px 2px 8px;
        color: rgba(27, 31, 35, 0.4);
      }

      .deletion .line-number {
        background-color: #ffdce0;
      }

      .insertion .line-number {
        background-color: #bef5cb;
      }

      .unmodified .line-number {
        background-color: rgba(230, 230, 230, 0.1);
      }

      .diff-section .line-number {
        background-color: #dbedff;
      }
    </style>
  </head>
  <body id="page">
    <table>
      <tbody>
$REPLACE_SECTION
      </tbody>
    </table>
  </body>
</html>

""".trimIndent()

fun FormattedDiff.toHtml(): String {
  val tableBody = lines.fold(StringBuilder()) { builder, diffLine ->
    builder.append(mapToTableRow(diffLine))
  }

  return template.replace(REPLACE_SECTION, tableBody.toString().trimEnd())
}

private fun mapToTableRow(diffLine: DiffLine): String {
  return when (diffLine) {
    is Marker.Text -> {
      """
        |        <tr class="diff-section">
        |          <td class="line-number">$ZERO_WIDTH_SPACE</td><td class="line-number">$ZERO_WIDTH_SPACE</td><td class="blob"></td>
        |        </tr>
        |
      """.trimMargin("|")
    }

    is Unmodified -> {
      val oldLineNumber = if (diffLine.oldLineNumber == 0) "" else diffLine.oldLineNumber.toString()
      val newLineNumber = if (diffLine.newLineNumber == 0) "" else diffLine.newLineNumber.toString()
      val htmlFriendlyLine = toHtmlFriendly(diffLine.text)
      """
        |        <tr class="unmodified">
        |          <td class="line-number">$oldLineNumber</td><td class="line-number">$newLineNumber</td><td class="blob">$NBSP$NBSP$htmlFriendlyLine</td>
        |        </tr>
        |
      """.trimMargin("|")
    }

    is Deletion -> {
      val oldLineNumber = diffLine.oldLineNumber
      val htmlFriendlyLine = toHtmlFriendly(diffLine.text)
      """
        |        <tr class="deletion">
        |          <td class="line-number">$oldLineNumber</td><td class="line-number"></td><td class="blob">-$NBSP$htmlFriendlyLine</td>
        |        </tr>
        |
      """.trimMargin("|")
    }

    is Insertion -> {
      val newLineNumber = diffLine.newLineNumber
      val htmlFriendlyLine = toHtmlFriendly(diffLine.text)
      """
        |        <tr class="insertion">
        |          <td class="line-number"></td><td class="line-number">$newLineNumber</td><td class="blob">+$NBSP$htmlFriendlyLine</td>
        |        </tr>
        |
      """.trimMargin("|")
    }

    ContentsEmpty -> {
      """
        |        <tr>
        |          <td>${LT}contents empty$GT</td>
        |        </tr>
      """.trimMargin("|")
    }

    is Marker.Binary -> {
      """
        |        <tr>
        |          <td>${LT}binary files differ$GT</td>
        |        </tr>
      """.trimMargin("|")
    }

    is FileModeChanged -> {
      """
        |        <tr>
        |          <td>${LT}file mode changed$GT</td>
        |        </tr>
      """.trimMargin("|")
    }
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
