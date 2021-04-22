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
  val comparisonResults = (this as HasChanges).comparisonResults
  val addedModifiedUnmodifiedRows = toRows(sourceCode, comparisonResults)
  val deletedRows = comparisonResults.filterIsInstance<Deleted>()
    .map { it.snippet.split(NEWLINE_CHAR).map { line -> deletedRowHtml(line) } }
    .flatten()

  return htmlTemplate(deletedRows + addedModifiedUnmodifiedRows)
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

private fun toRows(
  sourceCode: String,
  comparisonResults: List<ComparisonResult>
): List<String> {
  val addedFunctionRanges = addedFunctionRanges(comparisonResults)
  val modifiedFunctionRanges = modifiedFunctionRanges(comparisonResults)

  return sourceCode.split(NEWLINE_CHAR).mapIndexed { index, line ->
    val lineNumber = index + 1

    if (addedFunctionRanges.any { range -> lineNumber in range }) {
      addedRowHtml(lineNumber, line)
    } else if (modifiedFunctionRanges.any { range -> lineNumber in range }) {
      modifiedRowHtml(lineNumber, line)
    } else {
      unchangedRowHtml(lineNumber, line)
    }
  }
}

private fun addedFunctionRanges(comparisonResults: List<ComparisonResult>): List<IntRange> {
  return comparisonResults
    .filterIsInstance<Added>()
    .map { it.function.startLine..it.function.endLine }
}

private fun modifiedFunctionRanges(comparisonResults: List<ComparisonResult>): List<IntRange> {
  return comparisonResults
    .filterIsInstance<Modified>()
    .map { it.function.startLine..it.function.endLine }
}

private fun addedRowHtml(lineNumber: Int, line: String): String {
  return """
        <tr${classAttribute(CSS_CLASS_ADDED)}>
            <td>$lineNumber</td>
            <td>${padStartWithNbsp(line)}</td>
        </tr>
  """.trimIndent()
}

private fun modifiedRowHtml(lineNumber: Int, line: String): String {
  return """
       <tr${classAttribute(CSS_CLASS_MODIFIED)}>
           <td>$lineNumber</td>
           <td>${padStartWithNbsp(line)}</td>
       </tr>
  """.trimIndent()
}

private fun deletedRowHtml(line: String): String {
  return """
      <tr${classAttribute(CSS_CLASS_DELETED)}>
          <td></td>
          <td>$line</td>
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
