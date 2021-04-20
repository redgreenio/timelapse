package xyz.ragunath.soso

import java.util.Stack
import java.lang.Character.MIN_VALUE as NULL_CHAR

typealias Depth = Int // TODO Should we call this nesting?
typealias Length = Int // TODO Should we call this size?

data class Result(
  val depth: Depth,
  val length: Length
) {
  companion object {
    val EMPTY = Result(0, 0)
  }
}

object SoSo {
  private const val TOKEN_OPEN_CURLY = '{'
  private const val TOKEN_CLOSE_CURLY = '}'
  private const val TOKEN_FORWARD_SLASH = '/'
  private const val TOKEN_NEW_LINE = '\n' // TODO, this should be passed from outside
  private const val TOKEN_ASTERISK = '*'

  private const val FIND_DEPTH = 1
  private const val SKIP_SINGLE_LINE_COMMENT = 2
  private const val SKIP_MULTI_LINE_COMMENT = 3

  fun analyze(snippet: String): Result {
    val chars = snippet.toCharArray()
    val depthStack = Stack<Depth>()
    var previousChar = NULL_CHAR
    var maximumDepthCount = 0
    var lineCount = 1
    var mode = FIND_DEPTH

    for (char in chars) {
      val singleLineCommentFound = previousChar == TOKEN_FORWARD_SLASH && char == TOKEN_FORWARD_SLASH
      if (singleLineCommentFound) {
        mode = SKIP_SINGLE_LINE_COMMENT
      }

      val notSkippingSingleLineComment = !singleLineCommentFound && mode != SKIP_SINGLE_LINE_COMMENT
      val multilineCommentFound = previousChar == TOKEN_FORWARD_SLASH && char == TOKEN_ASTERISK
      if (notSkippingSingleLineComment && multilineCommentFound) {
        mode = SKIP_MULTI_LINE_COMMENT
      }

      if (mode == SKIP_SINGLE_LINE_COMMENT) {
        if (char == TOKEN_NEW_LINE) {
          lineCount++
          mode = FIND_DEPTH
        }
      } else if (mode == SKIP_MULTI_LINE_COMMENT) {
        if (char == TOKEN_NEW_LINE) {
          lineCount++
        }
        if (char == TOKEN_FORWARD_SLASH && previousChar == TOKEN_ASTERISK) {
          mode = FIND_DEPTH
        }
      } else if (mode == FIND_DEPTH) {
        when (char) {
          TOKEN_OPEN_CURLY -> with(depthStack) {
            if (isEmpty()) {
              maximumDepthCount = 1
              push(maximumDepthCount)
            } else {
              val depthCount = depthStack.peek() + 1
              if (depthCount > maximumDepthCount) {
                maximumDepthCount = depthCount
              }
              push(depthCount)
            }
          }

          TOKEN_CLOSE_CURLY -> depthStack.pop()
          TOKEN_NEW_LINE -> lineCount++
        }
      }

      previousChar = char
    }

    return Result(maximumDepthCount, lineCount)
  }
}
