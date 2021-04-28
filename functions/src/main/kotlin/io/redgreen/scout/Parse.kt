package io.redgreen.scout

import io.redgreen.scout.ScanMode.FIND_BLOCKS
import io.redgreen.scout.ScanMode.SKIP_MULTILINE_COMMENT
import io.redgreen.scout.ScanMode.SKIP_MULTILINE_STRING_LITERAL
import io.redgreen.scout.ScanMode.SKIP_SINGLE_LINE_COMMENT
import io.redgreen.scout.ScanMode.SKIP_STRING_LITERAL
import io.redgreen.scout.extensions.contents
import io.redgreen.scout.extensions.push
import io.redgreen.scout.extensions.top
import java.util.Stack

typealias Depth = Int

private enum class ScanMode {
  FIND_BLOCKS,
  SKIP_SINGLE_LINE_COMMENT,
  SKIP_MULTILINE_COMMENT,
  SKIP_STRING_LITERAL,
  SKIP_MULTILINE_STRING_LITERAL,
}

private const val TOKEN_OPEN_CURLY = '{'
private const val TOKEN_CLOSE_CURLY = '}'
private const val TOKEN_FORWARD_SLASH = '/'
private const val TOKEN_NEW_LINE = '\n'
private const val TOKEN_ASTERISK = '*'
private const val TOKEN_STRING_QUOTE = '"'
private const val TOKEN_MULTILINE_STRING = "\"\"\""

private const val PREVIOUS_CHARS_BUFFER_SIZE = 2

@SuppressWarnings("LoopWithTooManyJumpStatements", "LongMethod", "ComplexMethod", "NestedBlockDepth", "ReturnCount")
fun parse(snippet: String, lineNumberOffset: Int = 0): ParseResult {
  val snippetChars = snippet.toCharArray()
  val previousCharsBuffer = CharArray(PREVIOUS_CHARS_BUFFER_SIZE)

  var mode = FIND_BLOCKS
  var maximumDepth = 0
  var lineNumber = if (lineNumberOffset == 0) 1 else lineNumberOffset
  var startLineNumber = lineNumber
  val depthStack = Stack<Depth>()
  for (char in snippetChars) {
    if (char == TOKEN_NEW_LINE) {
      lineNumber++
    }

    val quoteFoundWhenLookingForBlocks = char == TOKEN_STRING_QUOTE && mode == FIND_BLOCKS
    if (quoteFoundWhenLookingForBlocks && isMultilineString(previousCharsBuffer, char)) {
      mode = SKIP_MULTILINE_STRING_LITERAL
      previousCharsBuffer.push(char)
      continue
    } else if (quoteFoundWhenLookingForBlocks) {
      mode = SKIP_STRING_LITERAL
      previousCharsBuffer.push(char)
      continue
    }

    when {
      isSingleLineComment(mode, previousCharsBuffer, char) -> mode = SKIP_SINGLE_LINE_COMMENT
      isMultilineComment(mode, previousCharsBuffer, char) -> mode = SKIP_MULTILINE_COMMENT
    }

    when (mode) {
      SKIP_STRING_LITERAL -> if (char == TOKEN_STRING_QUOTE) {
        mode = FIND_BLOCKS
      }

      SKIP_SINGLE_LINE_COMMENT -> if (char == TOKEN_NEW_LINE) {
        mode = FIND_BLOCKS
      }

      SKIP_MULTILINE_COMMENT -> if (previousCharsBuffer.top().get() == TOKEN_ASTERISK && char == TOKEN_FORWARD_SLASH) {
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
            return ParseResult.wellFormedFunction(startLineNumber, lineNumber, maximumDepth)
          }
        }
      }

      SKIP_MULTILINE_STRING_LITERAL -> if (isMultilineString(previousCharsBuffer, char)) {
        mode = FIND_BLOCKS
      }
    }

    previousCharsBuffer.push(char)
  }

  return ParseResult.nothing(startLineNumber, lineNumber)
}

private fun isSingleLineComment(
  currentScanMode: ScanMode,
  previousCharsBuffer: CharArray,
  char: Char
): Boolean {
  return previousCharsBuffer.top().isPresent &&
    previousCharsBuffer.top().get() == TOKEN_FORWARD_SLASH &&
    char == TOKEN_FORWARD_SLASH &&
    currentScanMode != SKIP_STRING_LITERAL
}

private fun isMultilineComment(
  currentScanMode: ScanMode,
  previousCharsBuffer: CharArray,
  char: Char
): Boolean {
  return previousCharsBuffer.top().isPresent &&
    previousCharsBuffer.top().get() == TOKEN_FORWARD_SLASH &&
    char == TOKEN_ASTERISK &&
    currentScanMode != SKIP_SINGLE_LINE_COMMENT
}

private fun isMultilineString(
  previousCharsBuffer: CharArray,
  char: Char
): Boolean {
  val bufferContents = previousCharsBuffer.contents()
  return bufferContents.isPresent &&
    "$bufferContents$char" == TOKEN_MULTILINE_STRING
}
