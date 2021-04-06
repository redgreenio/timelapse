package io.redgreen.timelapse.openrepo.view

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.redgreen.timelapse.openrepo.OpenRepoModel
import io.redgreen.timelapse.openrepo.data.RecentRepository
import io.redgreen.timelapse.openrepo.view.RecentRepositoriesStatus.LOADING
import io.redgreen.timelapse.openrepo.view.RecentRepositoriesStatus.NO_RECENT_REPOSITORIES
import io.redgreen.timelapse.openrepo.view.WelcomeMessage.Greeter
import io.redgreen.timelapse.openrepo.view.WelcomeMessage.Stranger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class OpenRepoViewRendererTest {
  private val view = mock<OpenRepoView>()
  private val viewRenderer = OpenRepoViewRenderer(view)

  @AfterEach
  fun teardown() {
    verifyNoMoreInteractions(view)
  }

  @Test
  fun `is should render start model`() {
    // given
    val start = OpenRepoModel.start()

    // when
    viewRenderer.render(start)

    // then
    verify(view).displayWelcomeMessage(Stranger)
    verify(view).displayRecentRepositoriesStatus(LOADING)
  }

  @Test
  fun `it should render cannot find git username`() {
    // given
    val cannotFindGitUsername = OpenRepoModel
      .start()
      .cannotFindGitUsername()

    // when
    viewRenderer.render(cannotFindGitUsername)

    // then
    verify(view).displayWelcomeMessage(Stranger)
    verify(view).displayRecentRepositoriesStatus(LOADING)
  }

  @Test
  fun `it should render git username found`() {
    // given
    val username = "Goushik"
    val gitUsernameFound = OpenRepoModel
      .start()
      .gitUsernameFound(username)

    // when
    viewRenderer.render(gitUsernameFound)

    // then
    verify(view).displayWelcomeMessage(Greeter(username))
    verify(view).displayRecentRepositoriesStatus(LOADING)
  }

  @Test
  fun `it should render no recent repositories found`() {
    // given
    val noRecentRepositories = OpenRepoModel
      .start()
      .cannotFindGitUsername()
      .noRecentRepositories()

    // when
    viewRenderer.render(noRecentRepositories)

    // then
    verify(view).displayWelcomeMessage(Stranger)
    verify(view).displayRecentRepositoriesStatus(NO_RECENT_REPOSITORIES)
  }

  @Test
  fun `it should render no recent repositories found in case of failure`() {
    // given
    val failedToGetRecentRepositories = OpenRepoModel
      .start()
      .cannotFindGitUsername()
      .failedToGetRecentRepositories()

    // when
    viewRenderer.render(failedToGetRecentRepositories)

    // then
    verify(view).displayWelcomeMessage(Stranger)
    verify(view).displayRecentRepositoriesStatus(NO_RECENT_REPOSITORIES)
  }

  @Test
  fun `it should render recent repositories if available`() {
    // given
    val recentRepositories = listOf(RecentRepository("~/IdeaProjects/JGit/.git"))
    val hasRecentRepositories = OpenRepoModel
      .start()
      .cannotFindGitUsername()
      .hasRecentRepositories(recentRepositories)

    // when
    viewRenderer.render(hasRecentRepositories)

    // then
    verify(view).displayWelcomeMessage(Stranger)
    verify(view).displayRecentRepositories(recentRepositories)
  }
}
