package io.redgreen.timelapse.diff

import io.redgreen.timelapse.diff.DiffLine.ContentsEmpty
import io.redgreen.timelapse.diff.DiffLine.Deletion
import io.redgreen.timelapse.diff.DiffLine.Insertion
import io.redgreen.timelapse.diff.DiffLine.Marker
import io.redgreen.timelapse.diff.DiffLine.Unmodified
import org.apache.commons.text.StringEscapeUtils

private val template = """
<html>
  <head>
    <style>
      table {
        width: 100%;
        border-collapse: collapse;
      }

      table td {
        font-family: "Fira Code", "JetBrains Mono", "Lucida Console";
        font-size: 15px;
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
  <table>
    <tbody>
<!-- Replace -->
    </tbody>
  </table>
</html>

""".trimIndent()

private const val REPLACE_SECTION = "<!-- Replace -->"

fun FormattedDiff.toHtml(): String {
  val tableBody = lines.fold(StringBuilder()) { builder, diffLine ->
    builder.append(mapToTableRow(diffLine))
  }

  return template.replace(REPLACE_SECTION, tableBody.toString().trimEnd())
}

private fun mapToTableRow(diffLine: DiffLine): String {
  return when (diffLine) {
    is Marker -> {
      """
        |      <tr class="diff-section">
        |        <td class="line-number">​</td><td class="line-number">​</td><td class="blob"></td>
        |      </tr>
        |
      """.trimMargin("|")
    }

    is Unmodified -> {
      val oldLineNumber = if (diffLine.oldLineNumber == 0) "" else diffLine.oldLineNumber.toString()
      val newLineNumber = if (diffLine.newLineNumber == 0) "" else diffLine.newLineNumber.toString()
      val nbspPaddedLine = padWithNbsp(diffLine.text)
      """
        |      <tr class="unmodified">
        |        <td class="line-number">$oldLineNumber</td><td class="line-number">$newLineNumber</td><td class="blob">&nbsp;&nbsp;$nbspPaddedLine</td>
        |      </tr>
        |
      """.trimMargin("|")
    }

    is Deletion -> {
      val oldLineNumber = diffLine.oldLineNumber
      val nbspPaddedLine = padWithNbsp(diffLine.text)
      """
        |      <tr class="deletion">
        |        <td class="line-number">$oldLineNumber</td><td class="line-number"></td><td class="blob">-&nbsp;$nbspPaddedLine</td>
        |      </tr>
        |
      """.trimMargin("|")
    }

    is Insertion -> {
      val newLineNumber = diffLine.newLineNumber
      val nbspPaddedLine = padWithNbsp(diffLine.text)
      """
        |      <tr class="insertion">
        |        <td class="line-number"></td><td class="line-number">$newLineNumber</td><td class="blob">+&nbsp;$nbspPaddedLine</td>
        |      </tr>
        |
      """.trimMargin("|")
    }

    ContentsEmpty -> {
      """
        |      <tr>
        |        <td>&lt;contents empty&gt;</td>
        |      </tr>
      """.trimMargin("|")
    }
  }
}

private fun padWithNbsp(line: String): String {
  val startSpaceCharsTrimmedLine = line.trimStart(' ')
  val nStartSpaceChars = line.length - startSpaceCharsTrimmedLine.length

  val htmlEscapedLine = StringEscapeUtils.escapeHtml4(line).trimStart(' ')
  return (1..nStartSpaceChars).joinToString("") { "&nbsp;" } + htmlEscapedLine
}
