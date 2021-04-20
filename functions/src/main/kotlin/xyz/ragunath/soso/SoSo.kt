package xyz.ragunath.soso

import java.util.Stack

typealias Depth = Int

data class Result(
  val depth: Depth
)

object SoSo {
  fun analyze(function: String): Result {
    val depthStack = Stack<Depth>()
    var maximumDepth = 0

    function
      .fold(0) { depth, char ->
        when (char) {
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

    return Result(maximumDepth)
  }
}
