package io.redgreen.timelapse.extendeddiff

class ExtendedDiffEngine private constructor(private val seedText: String) {
  companion object {
    fun newInstance(seedText: String): ExtendedDiffEngine {
      return ExtendedDiffEngine(seedText)
    }
  }
}
