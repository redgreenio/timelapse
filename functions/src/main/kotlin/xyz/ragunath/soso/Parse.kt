package xyz.ragunath.soso

import xyz.ragunath.soso.ScanMode.FIND_BLOCKS
import xyz.ragunath.soso.ScanMode.SKIP_MULTILINE_COMMENT
import xyz.ragunath.soso.ScanMode.SKIP_SINGLE_LINE_COMMENT
import xyz.ragunath.soso.ScanMode.SKIP_STRING_LITERAL
import java.util.Stack
import java.lang.Character.MIN_VALUE as NULL_CHAR

typealias Depth = Int

private enum class ScanMode {
  FIND_BLOCKS,
  SKIP_SINGLE_LINE_COMMENT,
  SKIP_MULTILINE_COMMENT,
  SKIP_STRING_LITERAL,
}

private const val TOKEN_OPEN_CURLY = '{'
private const val TOKEN_CLOSE_CURLY = '}'
private const val TOKEN_FORWARD_SLASH = '/'
private const val TOKEN_NEW_LINE = '\n'
private const val TOKEN_ASTERISK = '*'
private const val TOKEN_STRING_QUOTE = '"'

fun parse(snippet: String, lineNumberOffset: Int = 0): ParseResult {
  val snippetChars = snippet.toCharArray()
  var previousChar = NULL_CHAR

  var mode = FIND_BLOCKS
  var maximumDepth = 0
  var lineNumber = if (lineNumberOffset == 0) 1 else lineNumberOffset
  var startLineNumber = lineNumber
  val depthStack = Stack<Depth>()
  for (char in snippetChars) {
    if (char == TOKEN_NEW_LINE) {
      lineNumber++
    }

    if (char == TOKEN_STRING_QUOTE && mode == FIND_BLOCKS) {
      mode = SKIP_STRING_LITERAL
      previousChar = char
      continue
    }

    when {
      isSingleLineComment(mode, previousChar, char) -> mode = SKIP_SINGLE_LINE_COMMENT
      isMultilineComment(mode, previousChar, char) -> mode = SKIP_MULTILINE_COMMENT
    }

    when (mode) {
      SKIP_STRING_LITERAL -> if (char == TOKEN_STRING_QUOTE) {
        mode = FIND_BLOCKS
      }

      SKIP_SINGLE_LINE_COMMENT -> if (char == TOKEN_NEW_LINE) {
        mode = FIND_BLOCKS
      }

      SKIP_MULTILINE_COMMENT -> if (previousChar == TOKEN_ASTERISK && char == TOKEN_FORWARD_SLASH) {
        mode = FIND_BLOCKS
      }

      FIND_BLOCKS -> when (char) {
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
          if (depthStack.isEmpty()) {
            return ParseResult.malformedFunction(startLineNumber, lineNumber)
          }

          depthStack.pop()

          if (depthStack.isEmpty()) {
            return ParseResult.wellFormedFunction(
              startLineNumber,
              lineNumber,
              maximumDepth
            )
          }
        }
      }
    }

    previousChar = char
  }

  return ParseResult.nothing(startLineNumber, lineNumber)
}

private fun isSingleLineComment(currentScanMode: ScanMode, previousChar: Char, char: Char): Boolean =
  previousChar == TOKEN_FORWARD_SLASH && char == TOKEN_FORWARD_SLASH && currentScanMode != SKIP_STRING_LITERAL

private fun isMultilineComment(currentScanMode: ScanMode, previousChar: Char, char: Char): Boolean =
  previousChar == TOKEN_FORWARD_SLASH && char == TOKEN_ASTERISK && currentScanMode != SKIP_SINGLE_LINE_COMMENT
