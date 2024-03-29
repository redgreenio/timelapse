package io.redgreen.timelapse.openrepo

import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update
import io.redgreen.architecture.mobius.AsyncOp.Content
import io.redgreen.timelapse.openrepo.data.RecentGitRepository

object OpenRepoUpdate : Update<OpenRepoModel, OpenRepoEvent, OpenRepoEffect> {
  override fun update(
    model: OpenRepoModel,
    event: OpenRepoEvent
  ): Next<OpenRepoModel, OpenRepoEffect> {
    return when (event) {
      GitUsernameNotFound -> next(model)
      is GitUsernameFound -> next(model.gitUsernameFound(event.username))
      is ChooseGitRepository -> dispatch(setOf(DisplayFileChooser))
      is WorkingDirectoryChosen -> dispatch(setOf(DetectGitRepository(event.maybeRepositoryPath)))
      is GitRepositoryDetected -> dispatch(setOf(OpenGitRepository(event.repositoryPath)))
      is GitRepositoryNotDetected -> dispatch(setOf(ShowNotAGitRepositoryError(event.path)))
      is NoRecentRepositories -> next(model.noRecentRepositories())
      is HasRecentRepositories -> next(model.hasRecentRepositories(event.recentGitRepositories))
      is UnableToGetRecentRepositories -> next(model.failedToGetRecentRepositories())
      is OpenRecentRepository -> {
        val path = (model.recentGitRepositoriesAsyncOp as Content<List<RecentGitRepository>>).content[event.index].path
        dispatch(setOf(OpenGitRepository(path)))
      }
    }
  }
}
