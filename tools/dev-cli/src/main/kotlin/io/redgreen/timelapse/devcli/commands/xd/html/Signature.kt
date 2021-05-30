package io.redgreen.timelapse.devcli.commands.xd.html

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
