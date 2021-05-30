package io.redgreen.language.kotlin

data class Signature(
  val identifier: Identifier,
  val parameters: List<Parameter>
) {
  @SuppressWarnings("SpreadOperator") // Used for convenience in tests
  internal constructor(
    identifier: Identifier,
    vararg parameters: Parameter
  ) : this(identifier, listOf(*parameters))
}
