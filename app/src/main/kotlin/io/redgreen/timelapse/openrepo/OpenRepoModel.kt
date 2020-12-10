package io.redgreen.timelapse.openrepo

import io.redgreen.timelapse.mobius.AsyncOp
import io.redgreen.timelapse.mobius.AsyncOp.Companion.content
import io.redgreen.timelapse.mobius.AsyncOp.Companion.failure
import io.redgreen.timelapse.mobius.AsyncOp.Companion.inFlight

data class OpenRepoModel(
  val gitUsername: GitUsername,
  val recentRepositoriesAsyncOp: AsyncOp<List<RecentRepository>, GetRecentRepositories.Failure> = inFlight()
) {
  companion object {
    fun start(): OpenRepoModel {
      return OpenRepoModel(GitUsername.NotFound)
    }
  }

  fun cannotFindGitUsername(): OpenRepoModel {
    return copy(gitUsername = GitUsername.NotFound)
  }

  fun gitUsernameFound(
    username: String
  ): OpenRepoModel {
    return copy(gitUsername = GitUsername.Found(username))
  }

  fun noRecentRepositories(): OpenRepoModel {
    return copy(recentRepositoriesAsyncOp = content(emptyList()))
  }

  fun hasRecentRepositories(
    recentRepositories: List<RecentRepository>
  ): OpenRepoModel {
    return copy(recentRepositoriesAsyncOp = content(recentRepositories))
  }

  fun unableToGetRecentRepositories(): OpenRepoModel {
    return copy(recentRepositoriesAsyncOp = failure(GetRecentRepositories.Failure.Unknown))
  }
}
