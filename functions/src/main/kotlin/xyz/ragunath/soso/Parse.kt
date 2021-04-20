package xyz.ragunath.soso

import xyz.ragunath.soso.Mode.SCAN_DEPTH
import xyz.ragunath.soso.Mode.SKIP_MULTILINE_COMMENT
import xyz.ragunath.soso.Mode.SKIP_SINGLE_LINE_COMMENT
import xyz.ragunath.soso.Mode.SKIP_STRING_LITERAL
import xyz.ragunath.soso.Result.Nothing
import xyz.ragunath.soso.Result.WellFormedFunction
import java.util.Stack
import java.lang.Character.MIN_VALUE as NULL_CHAR

typealias Depth = Int

private const val TOKEN_OPEN_CURLY = '{'
private const val TOKEN_CLOSE_CURLY = '}'
private const val TOKEN_FORWARD_SLASH = '/'
private const val TOKEN_NEW_LINE = '\n'
private const val TOKEN_ASTERISK = '*'
private const val TOKEN_STRING_QUOTE = '"'

fun parse(snippet: String): Result {
  val snippetChars = snippet.toCharArray()
  var lastChar = NULL_CHAR

  var mode = SCAN_DEPTH
  var maximumDepth = 0
  var lineNumber = 1
  var startLineNumber = 0
  val endLineNumber: Int
  val depthStack = Stack<Depth>()
  for (char in snippetChars) {
    if (char == TOKEN_NEW_LINE) {
      lineNumber++
    }

    if (char == TOKEN_STRING_QUOTE && mode == SCAN_DEPTH) {
      mode = SKIP_STRING_LITERAL
      lastChar = char
      continue
    }

    when {
      isSingleLineComment(mode, lastChar, char) -> mode = SKIP_SINGLE_LINE_COMMENT
      isMultilineComment(mode, lastChar, char) -> mode = SKIP_MULTILINE_COMMENT
    }

    when (mode) {
      SKIP_STRING_LITERAL -> if (char == TOKEN_STRING_QUOTE) {
        mode = SCAN_DEPTH
      }

      SKIP_SINGLE_LINE_COMMENT -> if (char == TOKEN_NEW_LINE) {
        mode = SCAN_DEPTH
      }

      SKIP_MULTILINE_COMMENT -> if (lastChar == TOKEN_ASTERISK && char == TOKEN_FORWARD_SLASH) {
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
            return WellFormedFunction.with(
              startLineNumber,
              endLineNumber,
              maximumDepth
            )
          }
        }
      }
    }

    lastChar = char
  }

  return Nothing
}

private fun isSingleLineComment(
  currentMode: Mode,
  lastChar: Char,
  char: Char
): Boolean {
  return lastChar == TOKEN_FORWARD_SLASH && char == TOKEN_FORWARD_SLASH && currentMode != SKIP_STRING_LITERAL
}

private fun isMultilineComment(
  currentMode: Mode,
  lastChar: Char,
  char: Char
): Boolean {
  return lastChar == TOKEN_FORWARD_SLASH && char == TOKEN_ASTERISK &&
      currentMode != SKIP_SINGLE_LINE_COMMENT
}

enum class Mode {
  SCAN_DEPTH,
  SKIP_SINGLE_LINE_COMMENT,
  SKIP_MULTILINE_COMMENT,
  SKIP_STRING_LITERAL,
}
