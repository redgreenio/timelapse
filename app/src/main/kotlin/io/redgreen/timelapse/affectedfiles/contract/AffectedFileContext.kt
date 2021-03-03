package io.redgreen.timelapse.affectedfiles.contract

import io.redgreen.timelapse.git.CommitHash

data class AffectedFileContext(
  val filePath: String,
  val ancestor: CommitHash,
  val descendent: CommitHash
)
