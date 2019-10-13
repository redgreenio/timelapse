package xyz.ragunath.soso

data class Result(
  val startLine: Int,
  val endLine: Int,
  val depth: Depth
) {
  companion object {
    val EMPTY = with(0, 0, 0)

    fun with(startLine: Int, endLine: Int, depth: Int): Result {
      check(startLine >= 0) { "`startLine`: $startLine should be a positive integer" }
      check(endLine >= 0) { "`endLine`: $endLine should be a positive integer" }
      check(depth >= 0) { "`depth`: $depth should be a positive integer" }
      check(!(startLine == 0 && endLine == 0 && depth != 0)) { "`depth` must be zero for a non-existent function, but was `$depth`" }
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

  fun withOffset(offset: Int): Result =
    copy(startLine = offset + startLine, endLine = offset + endLine)
}
