package io.redgreen.timelapse.workbench.menu

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.EmptyMenu
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.NonEmptyMenu
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.NonEmptyMenu.ClearRecentsMenuItemViewModel
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.NonEmptyMenu.RecentRepositoryMenuItemViewModel
import org.junit.jupiter.api.Test

class OpenRecentMenuViewModelUseCaseTest {
  private val repositoriesStorage = mock<RecentGitRepositoriesStorage>()
  private val useCase = OpenRecentMenuViewModelUseCase(repositoriesStorage)

  @Test
  fun `it should return an empty view model when there are no recent repositories`() {
    // given
    whenever(repositoriesStorage.getRecentRepositories())
      .thenReturn(emptyList())

    // when
    val openRecentMenuViewModel = useCase.invoke()

    // then
    assertThat(openRecentMenuViewModel)
      .isEqualTo(EmptyMenu)
  }

  @Test
  fun `it should return recent repositories menu items and clear recent menu items if there are recent repositories`() {
    // given
    val recentRepositories = listOf("/Projects/shopping-app/.git", "/Projects/coffee/.git")
      .map(::RecentGitRepository)
    whenever(repositoriesStorage.getRecentRepositories())
      .thenReturn(recentRepositories)

    // when
    val openRecentMenuViewModel = useCase.invoke()

    // then
    assertThat(openRecentMenuViewModel)
      .isInstanceOf(NonEmptyMenu::class.java)

    assertThat((openRecentMenuViewModel as NonEmptyMenu).contents)
      .containsExactly(
        RecentRepositoryMenuItemViewModel("/Projects/shopping-app/.git"),
        RecentRepositoryMenuItemViewModel("/Projects/coffee/.git"),
        ClearRecentsMenuItemViewModel
      )
      .inOrder()
  }
}
