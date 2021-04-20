package io.redgreen.scout

import java.util.Optional

sealed class ParseResult(
  open val startLine: Int,
  open val endLine: Int
) {
  val length: Int
    get() = endLine - startLine + 1

  data class Nothing(
    override val startLine: Int,
    override val endLine: Int
  ) : ParseResult(startLine, endLine)

  data class WellFormedFunction(
    val name: Optional<Name> = Optional.empty(),
    override val startLine: Int,
    override val endLine: Int,
    val depth: Depth
  ) : ParseResult(startLine, endLine) {
    fun addName(name: String): ParseResult {
      return copy(name = Optional.of(Name(name)))
    }
  }

  data class MalformedFunction(
    override val startLine: Int,
    override val endLine: Int
  ) : ParseResult(startLine, endLine)

  companion object {
    fun nothing(startLine: Int, endLine: Int): Nothing {
      check(startLine, endLine)
      return Nothing(startLine, endLine)
    }

    fun wellFormedFunction(startLine: Int, endLine: Int, depth: Int): WellFormedFunction {
      return wellFormedFunction(Optional.empty(), startLine, endLine, depth)
    }

    fun wellFormedFunction(name: String, startLine: Int, endLine: Int, depth: Int): WellFormedFunction {
      return wellFormedFunction(Optional.of(Name(name)), startLine, endLine, depth)
    }

    private fun wellFormedFunction(name: Optional<Name>, startLine: Int, endLine: Int, depth: Int): WellFormedFunction {
      check(startLine, endLine)
      check(depth >= 0) { "`depth`: $depth should be a positive integer" }
      check(!(startLine == 0 && endLine == 0 && depth != 0)) { "`depth` must be zero for a non-existent function, but was `$depth`" }
      return WellFormedFunction(name, startLine, endLine, depth)
    }

    fun malformedFunction(startLine: Int, endLine: Int): MalformedFunction {
      check(startLine, endLine)
      return MalformedFunction(startLine, endLine)
    }

    private fun check(startLine: Int, endLine: Int) {
      check(startLine >= 0) { "`startLine`: $startLine should be a positive integer" }
      check(endLine >= 0) { "`endLine`: $endLine should be a positive integer" }
      check(startLine <= endLine) { "`startLine`: $startLine cannot be greater than `endLine`: $endLine" }
    }
  }
}
