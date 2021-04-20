package io.redgreen.timelapse.git.model

sealed class Ancestors {
  object None : Ancestors()
  data class One(val commitHash: CommitHash) : Ancestors()
  data class Many(val commitHashes: List<CommitHash>) : Ancestors()
}
