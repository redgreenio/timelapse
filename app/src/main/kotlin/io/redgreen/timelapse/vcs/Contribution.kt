package io.redgreen.timelapse.vcs

import io.redgreen.timelapse.git.model.Identity

data class Contribution(
  val author: Identity,
  val fraction: Double
)
