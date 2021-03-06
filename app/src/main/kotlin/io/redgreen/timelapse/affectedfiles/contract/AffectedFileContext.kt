package io.redgreen.timelapse.affectedfiles.contract

import io.redgreen.timelapse.core.CommitHash
import io.redgreen.timelapse.core.GitDirectory

data class AffectedFileContext(
  val gitDirectory: GitDirectory,
  val filePath: String,
  val descendent: CommitHash,
  val ancestor: CommitHash
)
