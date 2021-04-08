package io.redgreen.timelapse.router

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.openrepo.storage.PreferencesRecentGitRepositoriesStorage
import io.redgreen.timelapse.router.Destination.WELCOME_SCREEN
import io.redgreen.timelapse.router.Destination.WORKBENCH
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class SessionLauncherTest {
  @Nested
  inner class FirstTimeUser {
    private val recentGitRepositoriesStorage = PreferencesRecentGitRepositoriesStorage(
      preferencesNodeClass = FirstTimeUserSettingsNode::class
    )

    @BeforeEach
    fun setup() {
      recentGitRepositoriesStorage.clearSessionExitDestination()
      recentGitRepositoriesStorage.clearRecentRepositories()
    }

    @Test
    fun `it should launch the welcome screen (first time user)`() {
      // given
      val sessionLauncher = SessionLauncher(recentGitRepositoriesStorage) { false }
      val launchWelcomeScreenAction = mock<() -> Unit>()

      // when
      sessionLauncher.tryRestorePreviousSession(
        { fail("Expected to launch the welcome screen, but launched workbench instead.") },
        launchWelcomeScreenAction
      )

      // then
      verify(launchWelcomeScreenAction).invoke()
    }
  }

  class FirstTimeUserSettingsNode

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

  @Nested
  inner class UserExitedFromWorkbench {
    private val recentGitRepositoriesStorage = PreferencesRecentGitRepositoriesStorage(
      preferencesNodeClass = UserExitedFromWorkbenchSettingsNode::class
    )
    private val gitRepositoryPath = "/Users/varsha/twitter-clone/.git"

    @BeforeEach
    fun setup() {
      with(recentGitRepositoriesStorage) {
        setSessionExitDestination(WORKBENCH)
        update(RecentGitRepository(gitRepositoryPath))
      }
    }

    @Test
    fun `it should launch the workbench with previously opened repository`() {
      // given
      val checkIfIsGitRepo: (path: String) -> Boolean = { true }
      val launcher = SessionLauncher(recentGitRepositoriesStorage, checkIfIsGitRepo)
      val launchWorkbenchAction: (String) -> Unit = mock()

      // when
      launcher.tryRestorePreviousSession(
        launchWorkbenchAction,
        { fail("Expected to launch the workbench, but launched welcome screen instead.") }
      )

      // then
      verify(launchWorkbenchAction).invoke(gitRepositoryPath)
    }

    @Test
    fun `it should launch welcome screen if a last opened repository doesn't exist`() {
      // given
      val checkIfIsGitRepo: (path: String) -> Boolean = { false }
      val launcher = SessionLauncher(recentGitRepositoriesStorage, checkIfIsGitRepo)
      val launchWelcomeScreenAction = mock<() -> Unit>()

      // when
      launcher.tryRestorePreviousSession(
        { fail("Expected to launch the welcome screen action, but launched workbench instead.") },
        launchWelcomeScreenAction
      )

      // then
      verify(launchWelcomeScreenAction).invoke()
    }
  }

  class UserExitedFromWorkbenchSettingsNode
}
