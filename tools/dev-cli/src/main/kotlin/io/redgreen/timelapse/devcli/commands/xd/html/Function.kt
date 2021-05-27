package io.redgreen.timelapse.devcli.commands.xd.html

data class Function(
  val startLine: Int,
  val stopLine: Int,
  val identifier: String
) : LanguageElement
