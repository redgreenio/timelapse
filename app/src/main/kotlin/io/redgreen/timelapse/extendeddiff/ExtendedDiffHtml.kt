package io.redgreen.timelapse.extendeddiff

import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges

private const val NEWLINE_CHAR = '\n'

@SuppressWarnings("LongMethod")
fun ExtendedDiff.toHtml(): String {
  val result = (this as HasChanges).comparisonResults.first()

  return htmlTemplate(toRows(sourceCode, result))
}

private fun htmlTemplate(tableRows: String): String {
  return """
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
            $tableRows
            </tbody>
        </table>
        </body>
        </html>
  """.trimIndent()
}

private fun toRows(sourceCode: String, result: ComparisonResult): String {
  return when (result) {
    is Added -> offsetWithPadding(toAddedRows(sourceCode, result))
    is Deleted -> offsetWithPadding(toDeletedRows(result) + toUnchangedRows(sourceCode))
    is Modified -> offsetWithPadding(toModifiedRows(sourceCode, result))
  }
}

private fun toDeletedRows(deleted: Deleted): List<String> {
  return deleted.snippet.split(NEWLINE_CHAR).map { line ->
    """
      <tr class="deleted">
          <td></td>
          <td>$line</td>
      </tr>
    """.trimIndent()
  }
}

private fun toUnchangedRows(sourceCode: String): List<String> {
  return sourceCode
    .split(NEWLINE_CHAR)
    .mapIndexed { index, line ->
      val lineNumber = index + 1
      """
        <tr>
            <td>$lineNumber</td>
            <td>${padStartWithNbsp(line)}</td>
        </tr>
      """.trimIndent()
    }
}

private fun toModifiedRows(
  sourceCode: String,
  modified: Modified
): List<String> {
  val lines = sourceCode.split(NEWLINE_CHAR)
  val modifiedFunctionRange = modified.function.startLine..modified.function.endLine

  return lines.mapIndexed { index, line ->
    val lineNumber = index + 1
    val cssClassName = if (lineNumber in modifiedFunctionRange) "modified" else ""
    """
       <tr${classAttribute(cssClassName)}>
           <td>$lineNumber</td>
           <td>${padStartWithNbsp(line)}</td>
       </tr>
    """.trimIndent()
  }
}

private fun toAddedRows(
  sourceCode: String,
  added: Added
): List<String> {
  val lines = sourceCode.split(NEWLINE_CHAR)
  val addedRange = added.function.startLine..added.function.endLine

  return lines
    .mapIndexed { index, line ->
      val lineNumber = index + 1
      val cssClassName = if (lineNumber in addedRange) "added" else ""
      """
        <tr${classAttribute(cssClassName)}>
            <td>$lineNumber</td>
            <td>${padStartWithNbsp(line)}</td>
        </tr>
      """.trimIndent()
    }
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

private fun padStartWithNbsp(line: String): String {
  val prefix = line.takeWhile { it.isWhitespace() }
    .map { "&nbsp;" }
    .joinToString("")
  return prefix + line.trimStart()
}
