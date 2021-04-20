package xyz.ragunath.soso

import java.util.Stack

typealias Depth = Int

object SoSo {
  fun depthOf(code: String): Depth {
    val depthStack = Stack<Depth>()
    var maxDepth = 0

    code
      .fold(0) { depth, char ->
        when (char) {
          '{' -> {
            depthStack.push(depth + 1)
            if (depthStack.size > maxDepth) {
              maxDepth = depthStack.size
            }
            depthStack.peek()
          }

          '}' -> depthStack.pop()

          else -> depth
        }
      }

    return maxDepth
  }
}
