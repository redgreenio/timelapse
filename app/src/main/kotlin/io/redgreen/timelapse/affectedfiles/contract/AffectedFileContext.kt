package io.redgreen.timelapse.affectedfiles.contract

import io.redgreen.timelapse.git.CommitHash

data class AffectedFileContext(
  val repositoryPath: String,
  val filePath: String,
  val descendent: CommitHash,
  val ancestor: CommitHash
)
