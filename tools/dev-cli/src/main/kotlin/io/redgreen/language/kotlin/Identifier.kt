package io.redgreen.language.kotlin

data class Identifier(
  val text: String,
  val lineNumber: Int,
  val startIndex: Int
)
