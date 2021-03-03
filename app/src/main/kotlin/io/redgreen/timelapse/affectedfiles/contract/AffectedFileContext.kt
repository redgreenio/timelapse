package io.redgreen.timelapse.affectedfiles.contract

data class AffectedFileContext(
  val filePath: String,
  val ancestor: CommitHash,
  val descendent: CommitHash
)
