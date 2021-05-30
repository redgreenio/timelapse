package io.redgreen.language.kotlin

data class Function(
  val startLine: Int,
  val endLine: Int,
  val signature: Signature
) : LanguageElement
