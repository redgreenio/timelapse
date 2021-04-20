package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.FunctionScanner
import io.redgreen.scout.ParseResult.WellFormedFunction
import io.redgreen.scout.getParseResults
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added

fun compare(
  beforeSource: String,
  afterSource: String,
  scanner: FunctionScanner
): List<ComparisonResult> {
  val beforeFunctions = getParseResults(scanner::scan, beforeSource).filterIsInstance<WellFormedFunction>()
  val afterFunctions = getParseResults(scanner::scan, afterSource).filterIsInstance<WellFormedFunction>()

  return (afterFunctions - beforeFunctions).map(::Added)
}
