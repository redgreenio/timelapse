package io.redgreen.timelapse.openrepo

import com.spotify.mobius.First
import com.spotify.mobius.First.first
import com.spotify.mobius.Init

object OpenRepoInit : Init<OpenRepoModel, OpenRepoEffect> {
  override fun init(
    model: OpenRepoModel
  ): First<OpenRepoModel, OpenRepoEffect> =
    first(model, setOf(FindGitUsername, GetRecentRepositories))
}
