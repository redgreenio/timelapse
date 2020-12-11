package io.redgreen.timelapse.openrepo

import io.redgreen.timelapse.mobius.annotations.EffectEvent
import io.redgreen.timelapse.mobius.annotations.UiEvent
import io.redgreen.timelapse.openrepo.data.RecentRepository

sealed class OpenRepoEvent

@EffectEvent(FindGitUsername::class)
object CannotFindGitUsername : OpenRepoEvent()

@EffectEvent(FindGitUsername::class)
data class GitUsernameFound(
  val username: String
) : OpenRepoEvent()

@UiEvent
object ChooseGitRepository : OpenRepoEvent()

@EffectEvent(DisplayFileChooser::class)
data class GitRepositoryChosen(
  val maybeRepositoryPath: String
) : OpenRepoEvent()

@EffectEvent(AttemptOpenRepository::class)
data class GitRepositoryFound(
  val path: String
) : OpenRepoEvent()

@EffectEvent(AttemptOpenRepository::class)
data class GitRepositoryNotFound(
  val path: String
) : OpenRepoEvent()

@EffectEvent(GetRecentRepositories::class)
object NoRecentRepositories : OpenRepoEvent()

@EffectEvent(GetRecentRepositories::class)
data class HasRecentRepositories(
  val recentRepositories: List<RecentRepository>
) : OpenRepoEvent()

@EffectEvent(GetRecentRepositories::class)
object UnableToGetRecentRepositories : OpenRepoEvent()

@UiEvent
data class OpenRecentRepository(
  val path: String
) : OpenRepoEvent()
