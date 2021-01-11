package io.redgreen.timelapse.git

sealed class Ancestors {
  data class One(val commitId: String) : Ancestors()
  data class Many(val commitIds: List<String>) : Ancestors()
}
