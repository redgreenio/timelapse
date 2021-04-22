package io.redgreen.timelapse.extendeddiff

import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges

private const val NEWLINE_CHAR = '\n'

@SuppressWarnings("LongMethod")
fun ExtendedDiff.toHtml(): String {
  val isAdded = this is HasChanges && this.comparisonResults.first() is Added
  val isDeletion = this is HasChanges && this.comparisonResults.first() is Deleted

  return when {
    isAdded -> {
      val added = (this as HasChanges).comparisonResults.first() as Added

      """
        <html lang="en-US">
        <head>
            <style>
              .added {
                background-color: #e6ffed;
              }
              .modified {
                background-color: #dbedff80;
              }
              .deleted {
                background-color: #ffdce0;
              }
            </style>
        </head>
        <body>
        <table>
            <tbody>
            ${toRows(sourceCode, added)}
            </tbody>
        </table>
        </body>
        </html>
      """.trimIndent()
    }
    isDeletion -> {
      """
        <html lang="en-US">
        <head>
            <style>
              .added {
                background-color: #e6ffed;
              }
              .modified {
                background-color: #dbedff80;
              }
              .deleted {
                background-color: #ffdce0;
              }
            </style>
        </head>
        <body>
        <table>
            <tbody>
            <tr class="deleted">
                <td></td>
                <td>fun a() {</td>
            </tr>
            <tr class="deleted">
                <td></td>
                <td>}</td>
            </tr>
            <tr>
                <td>1</td>
                <td>fun b() {</td>
            </tr>
            <tr>
                <td>2</td>
                <td>}</td>
            </tr>
            </tbody>
        </table>
        </body>
        </html>
      """.trimIndent()
    }
    else -> {
      val modified = (this as HasChanges).comparisonResults.first() as Modified
      """
        <html lang="en-US">
        <head>
            <style>
              .added {
                background-color: #e6ffed;
              }
              .modified {
                background-color: #dbedff80;
              }
              .deleted {
                background-color: #ffdce0;
              }
            </style>
        </head>
        <body>
        <table>
            <tbody>
            ${toModifiedRows(sourceCode, modified)}
            </tbody>
        </table>
        </body>
        </html>
      """.trimIndent()
    }
  }
}

private fun toModifiedRows(sourceCode: String, modified: Modified): String {
  val lines = sourceCode.split(NEWLINE_CHAR)
  val modifiedFunctionRange = modified.function.startLine..modified.function.endLine

  val tableRows = lines.mapIndexed { index, line ->
    val lineNumber = index + 1
    val cssClassName = if (lineNumber in modifiedFunctionRange) "modified" else ""
    """
       <tr${classAttribute(cssClassName)}>
           <td>$lineNumber</td>
           <td>${padLeftWithNbsp(line)}</td>
       </tr>
    """.trimIndent()
  }

  return offsetWithPadding(tableRows)
}

private fun toRows(
  sourceCode: String,
  added: Added
): String {
  val lines = sourceCode.split(NEWLINE_CHAR)
  val addedRange = added.function.startLine..added.function.endLine

  val tableRows = lines
    .mapIndexed { index, line ->
      val lineNumber = index + 1
      val cssClassName = if (lineNumber in addedRange) "added" else ""
      """
        <tr${classAttribute(cssClassName)}>
            <td>$lineNumber</td>
            <td>${padLeftWithNbsp(line)}</td>
        </tr>
      """.trimIndent()
    }

  return offsetWithPadding(tableRows)
}

private fun classAttribute(cssClassName: String = ""): String {
  if (cssClassName.isEmpty()) {
    return ""
  }
  return " class=\"$cssClassName\""
}

private fun offsetWithPadding(tableRowSection: List<String>): String {
  return tableRowSection
    .joinToString("$NEWLINE_CHAR")
    .split(NEWLINE_CHAR)
    .mapIndexed { index, line ->
      if (index == 0) {
        line
      } else {
        "            $line"
      }
    }
    .joinToString("$NEWLINE_CHAR")
}

private fun padLeftWithNbsp(line: String): String {
  val prefix = line.takeWhile { it.isWhitespace() }
    .map { "&nbsp;" }
    .joinToString("")
  return prefix + line.trimStart()
}
