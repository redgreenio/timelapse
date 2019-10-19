package io.redgreen.scout

interface FunctionScanner {
  fun scan(snippet: String): List<PossibleFunction>
}
