package io.redgreen.timelapse.extendeddiff

import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges

private const val NEWLINE_CHAR = "\n"
private const val EMPTY_STRING = ""

private const val CSS_COLOR_ADDED = "#e6ffed"
private const val CSS_COLOR_MODIFIED = "#dbedff80"
private const val CSS_COLOR_DELETED = "#ffdce0"

private const val CSS_CLASS_ADDED = "added"
private const val CSS_CLASS_MODIFIED = "modified"
private const val CSS_CLASS_DELETED = "deleted"

private const val HTML_NBSP = "&nbsp;"

fun ExtendedDiff.toHtml(): String {
  val result = (this as HasChanges).comparisonResults.first()

  return htmlTemplate(toRows(sourceCode, result))
}

private fun htmlTemplate(tableRows: List<String>): String {
  return """
        <html lang="en-US">
        <head>
            <style>
              .added {
                background-color: $CSS_COLOR_ADDED;
              }
              .modified {
                background-color: $CSS_COLOR_MODIFIED;
              }
              .deleted {
                background-color: $CSS_COLOR_DELETED;
              }
            </style>
        </head>
        <body>
        <table>
            <tbody>
            ${offsetWithPadding(tableRows)}
            </tbody>
        </table>
        </body>
        </html>
  """.trimIndent()
}

private fun toRows(sourceCode: String, result: ComparisonResult): List<String> {
  return when (result) {
    is Added -> toAddedRows(sourceCode, result)
    is Deleted -> toDeletedRows(result) + toUnchangedRows(sourceCode)
    is Modified -> toModifiedRows(sourceCode, result)
  }
}

private fun toDeletedRows(deleted: Deleted): List<String> {
  return deleted.snippet.split(NEWLINE_CHAR).map { line ->
    """
      <tr class="$CSS_CLASS_DELETED">
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
      unchangedRowHtml(lineNumber, line)
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
    val cssClassName = if (lineNumber in modifiedFunctionRange) CSS_CLASS_MODIFIED else EMPTY_STRING
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
      if (lineNumber in addedRange) addedRowHtml(lineNumber, line) else unchangedRowHtml(lineNumber, line)
    }
}

private fun addedRowHtml(lineNumber: Int, line: String): String {
  return """
        <tr${classAttribute(CSS_CLASS_ADDED)}>
            <td>$lineNumber</td>
            <td>${padStartWithNbsp(line)}</td>
        </tr>
  """.trimIndent()
}

private fun unchangedRowHtml(lineNumber: Int, line: String): String {
  return """
        <tr>
            <td>$lineNumber</td>
            <td>${padStartWithNbsp(line)}</td>
        </tr>
  """.trimIndent()
}

private fun classAttribute(cssClassName: String): String {
  if (cssClassName.isEmpty()) {
    return EMPTY_STRING
  }
  return " class=\"$cssClassName\""
}

private fun offsetWithPadding(tableRowBlocks: List<String>): String {
  val tableRowsSection = tableRowBlocks
    .joinToString(NEWLINE_CHAR)

  return tableRowsSection
    .split(NEWLINE_CHAR)
    .mapIndexed { index, line ->
      val firstLine = index == 0
      if (firstLine) {
        line
      } else {
        "            $line"
      }
    }
    .joinToString(NEWLINE_CHAR)
}

private fun padStartWithNbsp(line: String): String {
  val prefix = line.takeWhile { it.isWhitespace() }
    .map { HTML_NBSP }
    .joinToString(EMPTY_STRING)
  return prefix + line.trimStart()
}
