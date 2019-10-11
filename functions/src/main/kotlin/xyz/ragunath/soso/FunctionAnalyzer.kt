package xyz.ragunath.soso

import java.util.Stack
import java.lang.Character.MIN_VALUE as NULL_CHAR

typealias Depth = Int
typealias Length = Int

private const val TOKEN_OPEN_CURLY = '{'
private const val TOKEN_CLOSE_CURLY = '}'
private const val TOKEN_FORWARD_SLASH = '/'
private const val TOKEN_NEW_LINE = '\n' // TODO, this should be passed from outside
private const val TOKEN_ASTERISK = '*'

private const val SCAN_DEPTH = 1
private const val SKIP_SINGLE_LINE_COMMENT = 2
private const val SKIP_MULTILINE_COMMENT = 3

fun analyze(snippet: String): List<Result> {
  val results = mutableListOf<Result>()
  val chars = snippet.toCharArray()
  var previousChar = NULL_CHAR

  var mode = SCAN_DEPTH
  var maximumDepthCount = 0
  var lineCount = 1
  var startLine = 0
  var endLine: Int
  val depthStack = Stack<Depth>()
  for (char in chars) {
    if (char == TOKEN_NEW_LINE) {
      lineCount++
    }

    when {
      isSingleLineComment(previousChar, char) -> mode = SKIP_SINGLE_LINE_COMMENT
      isMultilineComment(mode, previousChar, char) -> mode = SKIP_MULTILINE_COMMENT
    }

    when (mode) {
      SKIP_SINGLE_LINE_COMMENT -> if (char == TOKEN_NEW_LINE) {
        mode = SCAN_DEPTH
      }

      SKIP_MULTILINE_COMMENT -> if (previousChar == TOKEN_ASTERISK && char == TOKEN_FORWARD_SLASH) {
        mode = SCAN_DEPTH
      }

      SCAN_DEPTH -> when (char) {
        TOKEN_OPEN_CURLY -> {
          if (depthStack.isEmpty()) {
            maximumDepthCount = 1
            startLine = lineCount
            depthStack.push(maximumDepthCount)
          } else {
            val depthCount = depthStack.peek() + 1
            if (depthCount > maximumDepthCount) {
              maximumDepthCount = depthCount
            }
            depthStack.push(depthCount)
          }
        }

        TOKEN_CLOSE_CURLY -> {
          depthStack.pop()
          if (depthStack.isEmpty()) {
            endLine = lineCount
            results.add(
              Result.with(maximumDepthCount, calculateFunctionLength(startLine, endLine, maximumDepthCount), startLine, endLine)
            )
          }
        }
      }
    }

    previousChar = char
  }

  return results.toList()
}

private fun isSingleLineComment(
  previousChar: Char,
  char: Char
): Boolean =
  previousChar == TOKEN_FORWARD_SLASH && char == TOKEN_FORWARD_SLASH

private fun isMultilineComment(
  currentMode: Int,
  previousChar: Char,
  char: Char
): Boolean =
  previousChar == TOKEN_FORWARD_SLASH && char == TOKEN_ASTERISK && currentMode != SKIP_SINGLE_LINE_COMMENT

private fun calculateFunctionLength(
  startLine: Int,
  endLine: Int,
  depth: Int
): Int = if (endLine == startLine && depth == 0) {
  0
} else {
  endLine - startLine + 1
}
