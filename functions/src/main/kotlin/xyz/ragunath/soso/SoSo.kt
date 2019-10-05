package xyz.ragunath.soso

import java.util.Stack

typealias Depth = Int
typealias Length = Int

data class Result(
  val depth: Depth,
  val length: Length
) {
  companion object {
    val EMPTY = Result(0, 0)
  }
}

object SoSo {
  private val SINGLE_LINE_COMMENT_REGEX = Regex("//+\\s[^.!?]*")

  fun analyze(snippet: String): Result {
    if (snippet.isBlank()) return Result(0, 0)
    val sanitizedSnippet = sanitizeSnippet(snippet)
    return computeDepthAndLength(sanitizedSnippet)
  }

  private fun sanitizeSnippet(snippet: String): String {
    return snippet.split("\n")
      .joinToString("\n") { line ->
        line.replace(SINGLE_LINE_COMMENT_REGEX, "//")
      }
  }

  private fun computeDepthAndLength(sanitizedSnippet: String): Result {
    val depthStack = Stack<Depth>()
    var maximumDepth = 0
    var length = 1
    sanitizedSnippet
      .fold(0) { depth, char ->
        when (char) {
          '\n' -> {
            length += 1
            depth
          }

          '{' -> {
            depthStack.push(depth + 1)
            if (depthStack.peek() > maximumDepth) {
              maximumDepth = depthStack.size
            }
            depthStack.peek()
          }

          '}' -> depthStack.pop()

          else -> depth
        }
      }

    return Result(maximumDepth, if (maximumDepth != 0) length else 0)
  }
}
