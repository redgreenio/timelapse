package xyz.ragunath.soso

sealed class ParseResult(
  open val startLine: Int,
  open val endLine: Int
) {
  val length: Int
    get() = endLine - startLine + 1

  companion object {
    fun wellFormedFunction(startLine: Int, endLine: Int, depth: Int): WellFormedFunction {
      check(startLine >= 0) { "`startLine`: $startLine should be a positive integer" }
      check(endLine >= 0) { "`endLine`: $endLine should be a positive integer" }
      check(depth >= 0) { "`depth`: $depth should be a positive integer" }
      check(!(startLine == 0 && endLine == 0 && depth != 0)) { "`depth` must be zero for a non-existent function, but was `$depth`" }
      check(startLine <= endLine) { "`startLine`: $startLine cannot be greater than `endLine`: $endLine" }
      return WellFormedFunction(startLine, endLine, depth)
    }
  }

  data class Nothing(
    override val startLine: Int,
    override val endLine: Int
  ) : ParseResult(startLine, endLine)

  data class WellFormedFunction(
    override val startLine: Int,
    override val endLine: Int,
    val depth: Depth
  ) : ParseResult(startLine, endLine)

  data class MalformedFunction(
    override val startLine: Int,
    override val endLine: Int
  ) : ParseResult(startLine, endLine)
}
