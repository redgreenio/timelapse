package io.redgreen.timelapse.extendeddiff

import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted

internal class ComparisonResultComparator : Comparator<ComparisonResult> {
  override fun compare(result1: ComparisonResult, result2: ComparisonResult): Int {
    val result = result1.function.startLine - result2.function.startLine
    return if (result == 0 && result1 is Deleted) {
      -1
    } else if (result == 0 && result2 is Deleted) {
      1
    } else {
      result
    }
  }
}
