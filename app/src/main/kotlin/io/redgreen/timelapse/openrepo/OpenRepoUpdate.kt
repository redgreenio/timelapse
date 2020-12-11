package io.redgreen.timelapse.openrepo

import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update

object OpenRepoUpdate : Update<OpenRepoModel, OpenRepoEvent, OpenRepoEffect> {
  override fun update(
    model: OpenRepoModel,
    event: OpenRepoEvent
  ): Next<OpenRepoModel, OpenRepoEffect> {
    return when (event) {
      GitUsernameNotFound -> next(model)
      is GitUsernameFound -> next(model.gitUsernameFound(event.username))
      is ChooseGitRepository -> dispatch(setOf(DisplayFileChooser))
      is GitRepositoryChosen -> dispatch(setOf(DetectGitRepository(event.maybeRepositoryPath)))
      is GitRepositoryDetected -> dispatch(setOf(UpdateRecentRepositories(event.path), OpenRepository(event.path)))
      is GitRepositoryNotDetected -> dispatch(setOf(ShowNotAGitRepositoryError(event.path)))
      is NoRecentRepositories -> next(model.noRecentRepositories())
      is HasRecentRepositories -> next(model.hasRecentRepositories(event.recentRepositories))
      is UnableToGetRecentRepositories -> next(model.unableToGetRecentRepositories())
      is OpenRecentRepository -> dispatch(setOf(UpdateRecentRepositories(event.path), OpenRepository(event.path)))
    }
  }
}
