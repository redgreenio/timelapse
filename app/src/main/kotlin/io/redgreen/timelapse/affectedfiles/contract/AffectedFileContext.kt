package io.redgreen.timelapse.affectedfiles.contract

import io.redgreen.timelapse.git.model.CommitHash
import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.git.model.TrackedFilePath

data class AffectedFileContext(
  val gitDirectory: GitDirectory,
  val filePath: TrackedFilePath,
  val descendent: CommitHash,
  val ancestor: CommitHash
)
