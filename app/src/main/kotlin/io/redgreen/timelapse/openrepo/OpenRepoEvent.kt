package io.redgreen.timelapse.openrepo

import io.redgreen.architecture.mobius.annotations.EffectEvent
import io.redgreen.architecture.mobius.annotations.UiEvent
import io.redgreen.timelapse.openrepo.data.RecentGitRepository

sealed class OpenRepoEvent

@UiEvent
object ChooseGitRepository : OpenRepoEvent()

@UiEvent
data class OpenRecentRepository(
  val index: Int
) : OpenRepoEvent()

@EffectEvent(FindGitUsername::class)
object GitUsernameNotFound : OpenRepoEvent()

@EffectEvent(FindGitUsername::class)
data class GitUsernameFound(
  val username: String
) : OpenRepoEvent()

@EffectEvent(DisplayFileChooser::class)
data class WorkingDirectoryChosen(
  val maybeRepositoryPath: String
) : OpenRepoEvent()

@EffectEvent(DetectGitRepository::class)
data class GitRepositoryDetected(
  val repositoryPath: String
) : OpenRepoEvent()

@EffectEvent(DetectGitRepository::class)
data class GitRepositoryNotDetected(
  val path: String
) : OpenRepoEvent()

@EffectEvent(GetRecentRepositories::class)
object NoRecentRepositories : OpenRepoEvent()

@EffectEvent(GetRecentRepositories::class)
data class HasRecentRepositories(
  val recentGitRepositories: List<RecentGitRepository>
) : OpenRepoEvent()

@EffectEvent(GetRecentRepositories::class)
object UnableToGetRecentRepositories : OpenRepoEvent()
