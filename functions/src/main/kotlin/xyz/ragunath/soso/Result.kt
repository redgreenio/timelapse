package xyz.ragunath.soso

// FIXME(rj) 11-Oct-19 - Use lint to throw an error when someone uses a constructor without a factory function.
data class Result(
  val startLine: Int,
  val endLine: Int,
  val depth: Depth
) {
  companion object {
    fun with(startLine: Int, endLine: Int, depth: Int): Result {
      check(startLine.isPositive) { "`startLine`: $startLine should be a positive integer" }
      check(endLine.isPositive) { "`endLine`: $endLine should be a positive integer" }
      check(depth.isPositive) { "`depth`: $depth should be a positive integer" }
      val nonExistentFunction = startLine == 0 && endLine == 0
      check(!(nonExistentFunction && depth != 0)) { "`depth` must be zero for a non-existent function, but was `$depth`" }
      check(startLine <= endLine) { "`startLine`: $startLine cannot be greater than `endLine`: $endLine" }
      return Result(startLine, endLine, depth)
    }
  }

  val length: Int
    get() = if (startLine == endLine && depth == 0) {
      0
    } else {
      endLine - startLine + 1
    }
}

private val Int.isPositive: Boolean
  get() = this >= 0
