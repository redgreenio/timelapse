package io.redgreen.lang.kotlin

import io.redgreen.lang.ProgramElement

data class KtFunction(
  val identifier: String,
  val startLine: Int,
  val endLine: Int,
) : ProgramElement
