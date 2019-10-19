package xyz.ragunath.soso

sealed class ParseResult {
  data class Nothing(
    val startLine: Int = 0,
    val endLine: Int = 0
  ) : ParseResult() {
    val length: Int
      get() = endLine - startLine + 1
  }

  data class WellFormedFunction(
    val startLine: Int,
    val endLine: Int,
    val depth: Depth
  ) : ParseResult() {
    companion object {
      fun with(startLine: Int, endLine: Int, depth: Int): WellFormedFunction {
        check(startLine >= 0) { "`startLine`: $startLine should be a positive integer" }
        check(endLine >= 0) { "`endLine`: $endLine should be a positive integer" }
        check(depth >= 0) { "`depth`: $depth should be a positive integer" }
        check(!(startLine == 0 && endLine == 0 && depth != 0)) { "`depth` must be zero for a non-existent function, but was `$depth`" }
        check(startLine <= endLine) { "`startLine`: $startLine cannot be greater than `endLine`: $endLine" }
        return WellFormedFunction(startLine, endLine, depth)
      }
    }

    val length: Int
      get() = endLine - startLine + 1
  }

  data class MalformedFunction(
    val startLine: Int,
    val endLine: Int
  ) : ParseResult() {
    val length: Int
      get() = endLine - startLine + 1
  }
}
