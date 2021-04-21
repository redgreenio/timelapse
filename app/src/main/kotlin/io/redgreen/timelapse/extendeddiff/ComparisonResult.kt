package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.ParseResult.WellFormedFunction

sealed class ComparisonResult {
  data class Added(val function: WellFormedFunction) : ComparisonResult()
  data class Deleted(val function: WellFormedFunction, val snippet: String) : ComparisonResult()
  data class Modified(val function: WellFormedFunction) : ComparisonResult()
}
