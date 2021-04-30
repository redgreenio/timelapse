package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.ParseResult.WellFormedFunction
import io.redgreen.timelapse.diff.toHtmlFriendly
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Renamed
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Unmodified
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.NoChanges
import io.redgreen.timelapse.extendeddiff.LineNumber.CurrentSnapshot
import io.redgreen.timelapse.extendeddiff.LineNumber.PreviousSnapshot

private const val NEWLINE_CHAR = "\n"
private const val ZERO_WIDTH_SPACE = "\u200B"
private const val BLANK_LINE = ""

private const val CSS_COLOR_ADDED = "#e6ffed"
private const val CSS_COLOR_MODIFIED = "#dbedff80"
private const val CSS_COLOR_DELETED = "#ffdce0"
private const val CSS_COLOR_READABILITY = "#fafafa"

private const val CSS_CLASS_ADDED = "added"
private const val CSS_CLASS_MODIFIED = "modified"
private const val CSS_CLASS_DELETED = "deleted"
private const val CSS_CLASS_READABILITY = "readability"

private sealed class LineNumber {
  abstract val value: Int

  data class CurrentSnapshot(override val value: Int) : LineNumber()
  data class PreviousSnapshot(override val value: Int) : LineNumber()
  object ForReadability : LineNumber() {
    override val value: Int
      get() = -1
  }
}

fun ExtendedDiff.toHtml(): String {
  val htmlRows = if (this is HasChanges) {
    val unorderedComparisonResults = this.comparisonResults
    val comparisonResults = unorderedComparisonResults.sortedWith(ComparisonResultComparator())
    val sourceCodeLines = toLines(sourceCode)
    val linesNumbersAndLines = mutableLineNumbersAndLines(comparisonResults, sourceCodeLines)
    mergeUnchangedLines(sourceCodeLines, comparisonResults, linesNumbersAndLines)
    addBlankLinesToDeletedFunctionsForReadability(linesNumbersAndLines)
    mapToTableRows(linesNumbersAndLines, comparisonResults)
  } else {
    val sourceCodeLines = toLines((this as NoChanges).sourceCode)
    sourceCodeLines
      .mapIndexed { index, line -> unchangedRowHtml(index + 1, line) }
  }

  return htmlTemplate(htmlRows)
}

private fun addBlankLinesToDeletedFunctionsForReadability(
  linesNumbersAndLines: MutableList<Pair<LineNumber, String>>
) {
  val indicesOfBlankLines = linesNumbersAndLines
    .foldIndexed(mutableListOf<Int>()) { index, readabilityLineIndices, (lineNumber, _) ->
      val indexForReadabilityLineBefore =
        readabilityLineIndexBeforeDeletedFunction(index, lineNumber, linesNumbersAndLines)
      if (indexForReadabilityLineBefore != -1) {
        readabilityLineIndices.add(indexForReadabilityLineBefore)
      }

      val indexForReadabilityLineAfter =
        readabilityLineIndexAfterDeletedFunction(lineNumber, index, linesNumbersAndLines)
      if (indexForReadabilityLineAfter != -1) {
        readabilityLineIndices.add(indexForReadabilityLineAfter)
      }

      readabilityLineIndices
    }

  indicesOfBlankLines.reverse()
  indicesOfBlankLines.onEach { blankLineIndex ->
    linesNumbersAndLines.add(blankLineIndex, LineNumber.ForReadability to BLANK_LINE)
  }
}

private fun readabilityLineIndexAfterDeletedFunction(
  lineNumber: LineNumber,
  index: Int,
  linesNumbersAndLines: MutableList<Pair<LineNumber, String>>
): Int {
  val endFunction = isEndFunction(lineNumber, index, linesNumbersAndLines)
  val canAddAfterDeletedFunction = canAddReadabilityLineAfterDeletedFunction(index, linesNumbersAndLines)

  return if (endFunction && canAddAfterDeletedFunction) {
    index + 1
  } else {
    -1
  }
}

private fun readabilityLineIndexBeforeDeletedFunction(
  index: Int,
  lineNumber: LineNumber,
  linesNumbersAndLines: MutableList<Pair<LineNumber, String>>
): Int {
  val beginFunction = isBeginFunction(index, lineNumber, linesNumbersAndLines)
  val canAddBeforeDeletedFunction = canAddReadabilityLineBeforeDeletedFunction(index, linesNumbersAndLines)

  return if (beginFunction && canAddBeforeDeletedFunction) {
    index
  } else {
    -1
  }
}

private fun isEndFunction(
  lineNumber: LineNumber,
  index: Int,
  linesNumbersAndLines: MutableList<Pair<LineNumber, String>>
): Boolean {
  return lineNumber is PreviousSnapshot &&
    (index != linesNumbersAndLines.lastIndex && linesNumbersAndLines[index + 1].first !is PreviousSnapshot)
}

private fun isBeginFunction(
  index: Int,
  lineNumber: LineNumber,
  linesNumbersAndLines: MutableList<Pair<LineNumber, String>>
): Boolean {
  return lineNumber is PreviousSnapshot &&
    (index == 0 || linesNumbersAndLines[index - 1].first !is PreviousSnapshot)
}

private fun canAddReadabilityLineBeforeDeletedFunction(
  index: Int,
  linesNumbersAndLines: MutableList<Pair<LineNumber, String>>
): Boolean {
  return index > 0 && linesNumbersAndLines[index - 1].second.isNotBlank()
}

private fun canAddReadabilityLineAfterDeletedFunction(
  index: Int,
  linesNumbersAndLines: MutableList<Pair<LineNumber, String>>
): Boolean {
  return index != linesNumbersAndLines.lastIndex && linesNumbersAndLines[index + 1].second.isNotBlank()
}

private fun mapToTableRows(
  linesNumbersAndLines: MutableList<Pair<LineNumber, String>>,
  comparisonResults: List<ComparisonResult>
): List<String> {
  val addedFunctionRanges = addedFunctionRanges(comparisonResults)
  val modifiedFunctionRanges = modifiedFunctionRanges(comparisonResults)
  val renamedFunctionRanges = renamedFunctionRanges(comparisonResults)

  return linesNumbersAndLines
    .map { (lineNumber, line) ->
      when {
        isDeleted(lineNumber) -> deletedRowHtml(line)
        isInRangeOf(addedFunctionRanges, lineNumber) -> addedRowHtml(lineNumber.value, line)
        isInRangeOf(modifiedFunctionRanges, lineNumber) -> modifiedRowHtml(lineNumber.value, line)
        isInRangeOf(renamedFunctionRanges, lineNumber) -> modifiedRowHtml(lineNumber.value, line)
        lineNumber is LineNumber.ForReadability -> readabilityRowHtml()
        else -> unchangedRowHtml(lineNumber.value, line)
      }
    }
}

fun renamedFunctionRanges(comparisonResults: List<ComparisonResult>): List<IntRange> {
  return comparisonResults
    .filterIsInstance<Renamed>()
    .map { lineNumbersRange(it.function) }
}

private fun mergeUnchangedLines(
  sourceCodeLines: List<String>,
  comparisonResults: List<ComparisonResult>,
  linesNumbersAndLines: MutableList<Pair<LineNumber, String>>
) {
  val unchangedLineNumbers = unchangedLineNumbers(sourceCodeLines, linesNumbersAndLines)
  unchangedLineNumbers.onEach { unchangedLineNumber ->
    mergeUnchangedLine(sourceCodeLines, linesNumbersAndLines, comparisonResults, unchangedLineNumber)
  }
}

private fun mergeUnchangedLine(
  sourceCodeLines: List<String>,
  linesNumbersAndLines: MutableList<Pair<LineNumber, String>>,
  comparisonResults: List<ComparisonResult>,
  unchangedLineNumber: CurrentSnapshot
) {
  val deletedFunctionStartLinesAndLengths = comparisonResults
    .filterIsInstance<Deleted>()
    .map { it.function.startLine to it.function.endLine - it.function.startLine }

  val collidingLineNumberAndLength = deletedFunctionStartLinesAndLengths.find { (startLine, _) ->
    startLine == unchangedLineNumber.value
  }

  val foundCollision = collidingLineNumberAndLength != null

  if (foundCollision) {
    val collidingLineNumberAndLine = linesNumbersAndLines
      .find { it.first is PreviousSnapshot && it.first.value == unchangedLineNumber.value }

    if (collidingLineNumberAndLine != null) {
      val collidingLineAndLineNumberIndex = linesNumbersAndLines.indexOf(collidingLineNumberAndLine)
      val indexToInsert = collidingLineAndLineNumberIndex + collidingLineNumberAndLength!!.second
      val lineNumberLineRow = unchangedLineNumber to sourceCodeLines[unchangedLineNumber.value - 1]
      linesNumbersAndLines.add(indexToInsert + 1, lineNumberLineRow)
    }
  } else {
    val previousLineNumber = previousLineNumber(linesNumbersAndLines, unchangedLineNumber)
    if (previousLineNumber != null) {
      val indexToInsert = indexToInsert(linesNumbersAndLines, previousLineNumber)
      val lineNumberLineRow = unchangedLineNumber to sourceCodeLines[unchangedLineNumber.value - 1]
      linesNumbersAndLines.add(indexToInsert, lineNumberLineRow)
    } else {
      linesNumbersAndLines.add(0, unchangedLineNumber to sourceCodeLines[unchangedLineNumber.value - 1])
    }
  }
}

private fun indexToInsert(
  linesNumbersAndLines: MutableList<Pair<LineNumber, String>>,
  lineNumber: LineNumber
): Int {
  return linesNumbersAndLines
    .map(Pair<LineNumber, String>::first)
    .indexOf(lineNumber) + 1
}

private fun previousLineNumber(
  linesNumbersAndLines: MutableList<Pair<LineNumber, String>>,
  unchangedLineNumber: CurrentSnapshot
): LineNumber? {
  return linesNumbersAndLines
    .map(Pair<LineNumber, String>::first)
    .find { it is CurrentSnapshot && it.value == unchangedLineNumber.value - 1 }
}

private fun isInRangeOf(
  lineNumberRanges: List<IntRange>,
  lineNumber: LineNumber
): Boolean {
  return lineNumberRanges.any { range -> lineNumber.value in range }
}

private fun isDeleted(lineNumber: LineNumber): Boolean =
  lineNumber is PreviousSnapshot

private fun toLines(sourceCode: String): List<String> =
  sourceCode.split(NEWLINE_CHAR)

private fun mutableLineNumbersAndLines(
  comparisonResults: List<ComparisonResult>,
  sourceCodeLines: List<String>
): MutableList<Pair<LineNumber, String>> {
  return comparisonResults
    .map { result -> toLineNumberAndContent(result, sourceCodeLines) }
    .flatten()
    .toMutableList()
}

private fun unchangedLineNumbers(
  sourceCodeLines: List<String>,
  linesNumbersAndLines: MutableList<Pair<LineNumber, String>>
): List<CurrentSnapshot> {
  val allLineNumbers = (1..sourceCodeLines.size).toList()

  val lineNumbersInCurrentSnapshot = linesNumbersAndLines
    .map { it.first }
    .filterIsInstance<CurrentSnapshot>()
    .map { it.value }

  return (allLineNumbers - lineNumbersInCurrentSnapshot)
    .map(::CurrentSnapshot)
}

private fun toLineNumberAndContent(
  result: ComparisonResult,
  sourceCodeLines: List<String>
): List<Pair<LineNumber, String>> {
  return when (result) {
    is Added -> {
      lineNumbersRange(result.function).map { CurrentSnapshot(it) to sourceCodeLines[it - 1] }
    }

    is Modified -> {
      lineNumbersRange(result.function).map { CurrentSnapshot(it) to sourceCodeLines[it - 1] }
    }

    is Deleted -> {
      result
        .snippet
        .split(NEWLINE_CHAR)
        .mapIndexed { index, line -> PreviousSnapshot(result.function.startLine + index) to line }
    }

    is Unmodified -> {
      lineNumbersRange(result.function).map { CurrentSnapshot(it) to sourceCodeLines[it - 1] }
    }

    is Renamed -> {
      lineNumbersRange(result.function).map { CurrentSnapshot(it) to sourceCodeLines[it - 1] }
    }
  }
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
        .readability {
          background-color: $CSS_COLOR_READABILITY;
        }
        table {
          width: 100%;
          border-collapse: collapse;
        }
        table td {
          font-family: "monospace";
          font-size: small;
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

private fun lineNumbersRange(function: WellFormedFunction): IntRange =
  function.startLine..function.endLine

private fun addedFunctionRanges(comparisonResults: List<ComparisonResult>): List<IntRange> {
  return comparisonResults
    .filterIsInstance<Added>()
    .map { lineNumbersRange(it.function) }
}

private fun modifiedFunctionRanges(comparisonResults: List<ComparisonResult>): List<IntRange> {
  return comparisonResults
    .filterIsInstance<Modified>()
    .map { lineNumbersRange(it.function) }
}

private fun addedRowHtml(lineNumber: Int, line: String): String {
  return """
    <tr${classAttribute(CSS_CLASS_ADDED)}>
      <td>$lineNumber</td>
      <td>${toHtmlFriendly(line)}</td>
    </tr>
  """.trimIndent()
}

private fun modifiedRowHtml(lineNumber: Int, line: String): String {
  return """
    <tr${classAttribute(CSS_CLASS_MODIFIED)}>
      <td>$lineNumber</td>
      <td>${toHtmlFriendly(line)}</td>
    </tr>
  """.trimIndent()
}

private fun deletedRowHtml(line: String): String {
  return """
    <tr${classAttribute(CSS_CLASS_DELETED)}>
      <td>$ZERO_WIDTH_SPACE</td>
      <td>${toHtmlFriendly(line)}</td>
    </tr>
  """.trimIndent()
}

private fun readabilityRowHtml(): String {
  return """
    <tr${classAttribute(CSS_CLASS_READABILITY)}>
      <td>$ZERO_WIDTH_SPACE</td>
      <td></td>
    </tr>
  """.trimIndent()
}

private fun unchangedRowHtml(lineNumber: Int, line: String): String {
  return """
    <tr>
      <td>$lineNumber</td>
      <td>${toHtmlFriendly(line)}</td>
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
        "        $line"
      }
    }
    .joinToString(NEWLINE_CHAR)
}
