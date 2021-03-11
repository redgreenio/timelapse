package liftoff.affectedfiles.model

import io.redgreen.timelapse.git.model.CommitHash

data class AffectingCommit(
  val commitHash: CommitHash,
  val shortMessage: String,
)
