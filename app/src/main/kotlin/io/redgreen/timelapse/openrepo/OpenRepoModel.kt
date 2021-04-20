package io.redgreen.timelapse.openrepo

import io.redgreen.architecture.mobius.AsyncOp
import io.redgreen.architecture.mobius.AsyncOp.Companion.content
import io.redgreen.architecture.mobius.AsyncOp.Companion.failure
import io.redgreen.architecture.mobius.AsyncOp.Companion.idle
import io.redgreen.timelapse.openrepo.data.GitUsername
import io.redgreen.timelapse.openrepo.data.RecentGitRepository

data class OpenRepoModel(
  val gitUsername: GitUsername,
  val recentGitRepositoriesAsyncOp: AsyncOp<List<RecentGitRepository>, GetRecentRepositories.Failure> = idle()
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
    return copy(recentGitRepositoriesAsyncOp = content(emptyList()))
  }

  fun hasRecentRepositories(
    recentGitRepositories: List<RecentGitRepository>
  ): OpenRepoModel {
    return copy(recentGitRepositoriesAsyncOp = content(recentGitRepositories))
  }

  fun failedToGetRecentRepositories(): OpenRepoModel {
    return copy(recentGitRepositoriesAsyncOp = failure(GetRecentRepositories.Failure.Unknown))
  }
}
