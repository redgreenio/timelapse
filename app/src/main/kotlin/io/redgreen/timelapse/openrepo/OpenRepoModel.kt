package io.redgreen.timelapse.openrepo

import io.redgreen.architecture.mobius.AsyncOp
import io.redgreen.architecture.mobius.AsyncOp.Companion.content
import io.redgreen.architecture.mobius.AsyncOp.Companion.failure
import io.redgreen.architecture.mobius.AsyncOp.Companion.idle
import io.redgreen.timelapse.openrepo.data.GitUsername
import io.redgreen.timelapse.openrepo.data.RecentRepository

data class OpenRepoModel(
  val gitUsername: GitUsername,
  val recentRepositoriesAsyncOp: AsyncOp<List<RecentRepository>, GetRecentRepositories.Failure> = idle()
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

  fun failedToGetRecentRepositories(): OpenRepoModel {
    return copy(recentRepositoriesAsyncOp = failure(GetRecentRepositories.Failure.Unknown))
  }
}
