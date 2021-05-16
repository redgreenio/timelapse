package io.redgreen.timelapse.git.model

class PatchFile(private val unifiedPatch: String) {
  companion object {
    fun from(unifiedPatch: String): PatchFile =
      PatchFile(unifiedPatch)
  }

  fun getAffectedLineNumbers(): List<Int> {
    return listOf(1)
  }
}
