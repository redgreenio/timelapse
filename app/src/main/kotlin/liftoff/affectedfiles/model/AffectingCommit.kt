package liftoff.affectedfiles.model

import io.redgreen.timelapse.core.CommitHash

data class AffectingCommit(
  val commitHash: CommitHash,
  val shortMessage: String,
)
