package xyz.ragunath.soso

data class Result(
  val depth: Depth,
  val length: Length
) {
  companion object {
    val EMPTY = Result(0, 0)
  }
}
