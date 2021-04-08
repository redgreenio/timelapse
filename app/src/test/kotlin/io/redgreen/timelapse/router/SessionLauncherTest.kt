package io.redgreen.timelapse.router

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.openrepo.storage.PreferencesRecentGitRepositoriesStorage
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.router.Destination.WELCOME_SCREEN
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.Optional

class SessionLauncherTest {
  private val recentGitRepositoriesStorage = mock<RecentGitRepositoriesStorage>()

  @Test
  fun `it should launch the workbench if the user quit previous session with an active project`() {
    // given
    val gitRepositoryPath = "/Users/varsha/twitter-clone/.git"

    whenever(recentGitRepositoriesStorage.getLastOpenedRepository())
      .thenReturn(Optional.of(RecentGitRepository(gitRepositoryPath)))
    val checkIfIsGitRepo: (path: String) -> Boolean = { true }

    val launcher = SessionLauncher(recentGitRepositoriesStorage, checkIfIsGitRepo)

    val launchWelcomeScreenAction: () -> Unit = { fail("Expected to launch the Workbench action") }
    val launchWorkbenchAction: (String) -> Unit = mock()

    // when
    launcher.tryRestorePreviousSession(launchWorkbenchAction, launchWelcomeScreenAction)

    // then
    verify(launchWorkbenchAction).invoke(gitRepositoryPath)
  }

  @Test
  fun `it should launch the welcome screen for a first time user`() {
    // given
    whenever(recentGitRepositoriesStorage.getLastOpenedRepository())
      .thenReturn(Optional.empty())

    val launcher = SessionLauncher(recentGitRepositoriesStorage) {
      fail("Do not perform this check if there is no last opened repository")
    }

    val launchWelcomeScreenAction = mock<() -> Unit>()
    val launchWorkbenchAction: (String) -> Unit = { fail("Expected to launch the Welcome Screen action") }

    // when
    launcher.tryRestorePreviousSession(launchWorkbenchAction, launchWelcomeScreenAction)

    // then
    verify(launchWelcomeScreenAction).invoke()
  }

  @Test
  fun `it should launch the welcome screen if a last opened repository entry exists but the location doesn't exist`() {
    // given
    whenever(recentGitRepositoriesStorage.getLastOpenedRepository())
      .thenReturn(Optional.of(RecentGitRepository("/Users/varsha/twitter-clone/.git")))

    val launcher = SessionLauncher(recentGitRepositoriesStorage) {
      false
    }

    val launchWelcomeScreenAction = mock<() -> Unit>()
    val launchWorkbenchAction: (String) -> Unit = { fail("Expected to launch the Welcome Screen action") }

    // when
    launcher.tryRestorePreviousSession(launchWorkbenchAction, launchWelcomeScreenAction)

    // then
    verify(launchWelcomeScreenAction).invoke()
  }

  @Nested
  inner class UserExitedFromWelcomeScreen {
    private val recentGitRepositoriesStorage = PreferencesRecentGitRepositoriesStorage(
      preferencesNodeClass = UserExitedFromWelcomeScreenSettingsNode::class
    )
    private val sessionLauncher = SessionLauncher(recentGitRepositoriesStorage) { true }
    private val launchWelcomeScreenAction = mock<() -> Unit>()

    @BeforeEach
    fun setup() {
      recentGitRepositoriesStorage.setSessionExitDestination(WELCOME_SCREEN)
    }

    @Test
    fun `it should launch the welcome screen (no recent repositories)`() {
      // given
      recentGitRepositoriesStorage.clearRecentRepositories()

      // when
      sessionLauncher.tryRestorePreviousSession(
        { fail("Expected to launch the welcome screen, but launched workbench instead.") },
        launchWelcomeScreenAction
      )

      // then
      verify(launchWelcomeScreenAction).invoke()
    }

    @Test
    fun `it should launch the welcome screen (has recent repositories)`() {
      // given
      recentGitRepositoriesStorage.update(RecentGitRepository("/Users/JackSparrow/black-pearl/.git"))

      // when
      sessionLauncher.tryRestorePreviousSession(
        { fail("Expected to launch the welcome screen, but launched workbench instead.") },
        launchWelcomeScreenAction
      )

      // then
      verify(launchWelcomeScreenAction).invoke()
    }
  }

  class UserExitedFromWelcomeScreenSettingsNode
}
