package io.redgreen.timelapse.devcli.commands.xd.html

data class Function(
  val startLine: Int,
  val endLine: Int,
  val signature: Signature
) : LanguageElement
