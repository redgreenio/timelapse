package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.ParseResult.WellFormedFunction

sealed class ComparisonResult {
  abstract val function: WellFormedFunction

  data class Added(override val function: WellFormedFunction) : ComparisonResult()
  data class Deleted(override val function: WellFormedFunction, val snippet: String) : ComparisonResult()
  data class Modified(override val function: WellFormedFunction) : ComparisonResult()
}
