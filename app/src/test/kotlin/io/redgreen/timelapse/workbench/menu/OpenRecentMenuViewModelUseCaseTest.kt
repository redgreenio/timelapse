package io.redgreen.timelapse.workbench.menu

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.EmptyMenu
import org.junit.jupiter.api.Test

class OpenRecentMenuViewModelUseCaseTest {
  @Test
  fun `it should return an empty view model when there are no recent repositories`() {
    // given
    val repositoriesStorage = mock<RecentGitRepositoriesStorage>()
    whenever(repositoriesStorage.getRecentRepositories())
      .thenReturn(emptyList())

    val useCase = OpenRecentMenuViewModelUseCase(repositoriesStorage)

    // when
    val openRecentMenuViewModel = useCase.invoke()

    // then
    assertThat(openRecentMenuViewModel)
      .isEqualTo(EmptyMenu)
  }
}
