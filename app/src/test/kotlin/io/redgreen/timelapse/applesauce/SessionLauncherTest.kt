package io.redgreen.timelapse.applesauce

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
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
    verify(launchWelcomeScreenAction).invoke()
  }
}
