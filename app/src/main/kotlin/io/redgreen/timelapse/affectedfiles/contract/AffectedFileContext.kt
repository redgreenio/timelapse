package io.redgreen.timelapse.affectedfiles.contract

import io.redgreen.timelapse.core.GitDirectory
import io.redgreen.timelapse.git.CommitHash

data class AffectedFileContext(
  val gitDirectory: GitDirectory,
  val filePath: String,
  val descendent: CommitHash,
  val ancestor: CommitHash
)
