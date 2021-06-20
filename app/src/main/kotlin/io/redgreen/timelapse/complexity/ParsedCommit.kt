package io.redgreen.timelapse.complexity

import io.redgreen.timelapse.git.model.CommitHash
import io.redgreen.timelapse.git.model.Identity
import java.time.ZonedDateTime

data class ParsedCommit(
  val commitHash: CommitHash,
  val summary: String,
  val author: Identity,
  val authored: ZonedDateTime,
  val committer: Identity,
  val committed: ZonedDateTime,
  val filePath: String,
  val stats: Stats,
)
