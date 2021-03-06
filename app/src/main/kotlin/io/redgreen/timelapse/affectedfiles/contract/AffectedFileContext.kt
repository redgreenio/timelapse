package io.redgreen.timelapse.affectedfiles.contract

import io.redgreen.timelapse.core.CommitHash
import io.redgreen.timelapse.core.GitDirectory
import io.redgreen.timelapse.core.TrackedFilePath

data class AffectedFileContext(
  val gitDirectory: GitDirectory,
  val filePath: TrackedFilePath,
  val descendent: CommitHash,
  val ancestor: CommitHash
)
