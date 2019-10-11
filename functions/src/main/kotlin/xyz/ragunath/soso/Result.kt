package xyz.ragunath.soso

data class Result(
  val depth: Depth,
  val length: Length,
  val startLine: Int
) {
  companion object {
    val EMPTY = with(0, 0, 0)

    fun with(depth: Int, length: Int, startLine: Int): Result =
      Result(depth, length, startLine)
  }
}
