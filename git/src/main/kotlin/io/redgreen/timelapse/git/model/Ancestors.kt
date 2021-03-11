package io.redgreen.timelapse.git.model

sealed class Ancestors {
  object None : Ancestors()
  data class One(val commitId: String) : Ancestors()
  data class Many(val commitIds: List<String>) : Ancestors()
}
