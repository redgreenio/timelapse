package io.redgreen.timelapse.applesauce

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import org.junit.jupiter.api.Test
import java.util.Optional

class SessionLauncherTest {
  @Test
  fun `it should launch the workbench if the user quit previous session with an active project`() {
    // given
    val gitRepositoryPath = "/Users/varsha/twitter-clone/.git"

    val recentGitRepositoriesStorage = mock<RecentGitRepositoriesStorage>()
    whenever(recentGitRepositoriesStorage.getLastOpenedRepository())
      .thenReturn(Optional.of(RecentGitRepository(gitRepositoryPath)))
    val checkIfIsGitRepo: (path: String) -> Boolean = { true }

    val launcher = SessionLauncher(recentGitRepositoriesStorage, checkIfIsGitRepo)

    // when
    val launchWorkbenchAction = mock<(gitRepositoryPath: String) -> Unit>()
    launcher.tryRestorePreviousSession(launchWorkbenchAction)

    // then
    verify(launchWorkbenchAction).invoke(gitRepositoryPath)
    verifyNoMoreInteractions(launchWorkbenchAction)
  }
}
