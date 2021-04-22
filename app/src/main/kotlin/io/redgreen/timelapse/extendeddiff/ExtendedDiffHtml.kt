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
  val isModified = this is HasChanges && this.comparisonResults.first() is Modified

  return when {
    isAdded || isModified -> {
      val result = (this as HasChanges).comparisonResults.first()

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
            ${toRows(sourceCode, result)}
            </tbody>
        </table>
        </body>
        </html>
      """.trimIndent()
    }
    isDeletion -> {
      val sourceCode = (this as HasChanges).sourceCode
      val deleted = this.comparisonResults.first() as Deleted

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
            ${toRows(sourceCode, deleted)}
            </tbody>
        </table>
        </body>
        </html>
      """.trimIndent()
    }
    else -> TODO()
  }
}

private fun toRows(sourceCode: String, result: ComparisonResult): String {
  return when (result) {
    is Added -> toAddedRows(sourceCode, result)
    is Deleted -> toDeletedRows(sourceCode, result)
    is Modified -> toModifiedRows(sourceCode, result)
  }
}

private fun toDeletedRows(sourceCode: String, deleted: Deleted): String {
  val unmodifiedRowsNew = sourceCode
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

  val unmodifiedRows = offsetWithPadding(unmodifiedRowsNew)

  val newDeletedRowsWithoutOffset = deleted.snippet.split(NEWLINE_CHAR).map { line ->
    """
      <tr class="deleted">
          <td></td>
          <td>$line</td>
      </tr>
    """.trimIndent()
  }

  val deletedRows = offsetWithPadding(newDeletedRowsWithoutOffset)

  return """$deletedRows
            $unmodifiedRows"""
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
           <td>${padStartWithNbsp(line)}</td>
       </tr>
    """.trimIndent()
  }

  return offsetWithPadding(tableRows)
}

private fun toAddedRows(
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
            <td>${padStartWithNbsp(line)}</td>
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

private fun padStartWithNbsp(line: String): String {
  val prefix = line.takeWhile { it.isWhitespace() }
    .map { "&nbsp;" }
    .joinToString("")
  return prefix + line.trimStart()
}
