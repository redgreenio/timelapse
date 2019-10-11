package xyz.ragunath.soso

import java.util.Stack
import java.lang.Character.MIN_VALUE as NULL_CHAR

typealias Depth = Int
typealias Length = Int

private const val TOKEN_OPEN_CURLY = '{'
private const val TOKEN_CLOSE_CURLY = '}'
private const val TOKEN_FORWARD_SLASH = '/'
private const val TOKEN_NEW_LINE = '\n'
private const val TOKEN_ASTERISK = '*'

private const val SCAN_DEPTH = 1
private const val SKIP_SINGLE_LINE_COMMENT = 2
private const val SKIP_MULTILINE_COMMENT = 3

fun analyze(snippet: String): List<Result> {
  val results = mutableListOf<Result>()
  val snippetChars = snippet.toCharArray()
  var lastKnownChar = NULL_CHAR

  var mode = SCAN_DEPTH
  var maximumDepth = 0
  var lineNumber = 1
  var startLineNumber = 0
  var endLineNumber: Int
  val depthStack = Stack<Depth>()
  for (char in snippetChars) {
    if (char == TOKEN_NEW_LINE) {
      lineNumber++
    }

    when {
      isSingleLineComment(lastKnownChar, char) -> mode = SKIP_SINGLE_LINE_COMMENT
      isMultilineComment(mode, lastKnownChar, char) -> mode = SKIP_MULTILINE_COMMENT
    }

    when (mode) {
      SKIP_SINGLE_LINE_COMMENT -> if (char == TOKEN_NEW_LINE) {
        mode = SCAN_DEPTH
      }

      SKIP_MULTILINE_COMMENT -> if (lastKnownChar == TOKEN_ASTERISK && char == TOKEN_FORWARD_SLASH) {
        mode = SCAN_DEPTH
      }

      SCAN_DEPTH -> when (char) {
        TOKEN_OPEN_CURLY -> {
          if (depthStack.isEmpty()) {
            maximumDepth = 1
            startLineNumber = lineNumber
            depthStack.push(maximumDepth)
          } else {
            val depth = depthStack.peek() + 1
            if (depth > maximumDepth) {
              maximumDepth = depth
            }
            depthStack.push(depth)
          }
        }

        TOKEN_CLOSE_CURLY -> {
          depthStack.pop()

          if (depthStack.isEmpty()) {
            endLineNumber = lineNumber
            val result = Result.with(
              maximumDepth,
              calculateFunctionLength(startLineNumber, endLineNumber, maximumDepth),
              startLineNumber,
              endLineNumber
            )
            results.add(result)
          }
        }
      }
    }

    lastKnownChar = char
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
): Int = if (startLine == endLine && depth == 0) {
  0
} else {
  endLine - startLine + 1
}
