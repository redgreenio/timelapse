package io.redgreen.timelapse.openrepo

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.redgreen.architecture.mobius.testCase
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.openrepo.view.OpenRepoView
import io.redgreen.timelapse.platform.ImmediateSchedulersProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.Optional

class OpenRepoEffectHandlerTest {
  private val gitDetector = mock<GitDetector>()
  private val view = mock<OpenRepoView>()
  private val recentGitRepositoriesStorage = mock<RecentGitRepositoriesStorage>()

  private val testCase = OpenRepoEffectHandlerFactory(
    gitDetector,
    recentGitRepositoriesStorage,
    view,
    ImmediateSchedulersProvider
  )
    .instance()
    .testCase()

  @AfterEach
  fun teardown() {
    testCase.dispose()
  }

  @Test
  fun `it should return name found if a git global username is available`() {
    // given
    val globalUsername = "Ajay"
    whenever(gitDetector.globalUsername())
      .thenReturn(Optional.of(globalUsername))

    // when
    testCase.dispatch(FindGitUsername)

    // then
    testCase
      .assertOutgoingEvents(GitUsernameFound(globalUsername))
  }

  @Test
  fun `it should return username not found if a git global username is unavailable`() {
    // given
    whenever(gitDetector.globalUsername())
      .thenReturn(Optional.empty())

    // when
    testCase.dispatch(FindGitUsername)

    // then
    testCase
      .assertOutgoingEvents(GitUsernameNotFound)
  }

  @Test
  fun `it should return username not found if an exception is thrown`() {
    // given
    whenever(gitDetector.globalUsername())
      .thenThrow(RuntimeException("Something went wrong (´・_・`)"))

    // when
    testCase.dispatch(FindGitUsername)

    // then
    testCase
      .assertOutgoingEvents(GitUsernameNotFound)
  }

  @Test
  fun `it should show a file chooser`() {
    // when
    testCase.dispatch(DisplayFileChooser)

    // then
    verify(view).displayFileChooser()
    verifyNoMoreInteractions(view)

    testCase
      .assertNoOutgoingEvents()
  }

  @Test
  fun `it should return repository not detected if the path does not point to a git repository`() {
    // given
    val path = "/non/existent/path"

    // when
    testCase.dispatch(DetectGitRepository(path))

    // then
    testCase
      .assertOutgoingEvents(GitRepositoryNotDetected(path))
  }

  @Test
  fun `it should open a detected repository`() {
    // given
    val repositoryPath = "~/IdeaProjects/timelapse"

    // when
    testCase.dispatch(OpenGitRepository(repositoryPath))

    // then
    verify(view).closeWelcomeStage()
    verify(view).openGitRepository(repositoryPath)
    verifyNoMoreInteractions(view)

    testCase
      .assertNoOutgoingEvents()
  }

  @Test
  fun `it should show not a git repository message`() {
    // given
    val nonExistentPath = "/non/existent/path"

    // when
    testCase.dispatch(ShowNotAGitRepositoryError(nonExistentPath))

    // then
    verify(view).showNotAGitRepositoryError(nonExistentPath)
    verifyNoMoreInteractions(view)

    testCase
      .assertNoOutgoingEvents()
  }

  @Test
  fun `it should return no recent repositories if the list is empty`() {
    // given
    whenever(recentGitRepositoriesStorage.getRecentRepositories())
      .thenReturn(emptyList())

    // when
    testCase.dispatch(GetRecentRepositories)

    // then
    testCase
      .assertOutgoingEvents(NoRecentRepositories)
  }

  @Test
  fun `it should return a list of recent repositories if they are available`() {
    // given
    val recentRepositories = listOf(
      "~/IdeaProjects/timelapse/.git",
      "~/IdeaProjects/square/retrofit/.git"
    )
      .map(::RecentGitRepository)

    whenever(recentGitRepositoriesStorage.getRecentRepositories())
      .thenReturn(recentRepositories)

    // when
    testCase.dispatch(GetRecentRepositories)

    // then
    testCase
      .assertOutgoingEvents(HasRecentRepositories(recentRepositories))
  }

  @Test
  fun `it should return unable to get recent repositories if there is an exception`() {
    // given
    whenever(recentGitRepositoriesStorage.getRecentRepositories())
      .thenThrow(RuntimeException("Error retrieving recent repositories :("))

    // when
    testCase.dispatch(GetRecentRepositories)

    // then
    testCase
      .assertOutgoingEvents(UnableToGetRecentRepositories)
  }
}
