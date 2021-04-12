package io.redgreen.timelapse.workbench.menu

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemViewModel.RecentRepository
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.Empty
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.NonEmpty
import org.junit.jupiter.api.Test

class OpenRecentMenuViewModelUseCaseTest {
  private val repositoriesStorage = mock<RecentGitRepositoriesStorage>()
  private val useCase = OpenRecentMenuViewModelUseCase(repositoriesStorage) { true }

  @Test
  fun `it should return an empty view model when there are no recent repositories`() {
    // given
    whenever(repositoriesStorage.getRecentRepositories())
      .thenReturn(emptyList())

    // when
    val openRecentMenuViewModel = useCase.invoke()

    // then
    assertThat(openRecentMenuViewModel)
      .isEqualTo(Empty)
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
      .isInstanceOf(NonEmpty::class.java)

    assertThat((openRecentMenuViewModel as NonEmpty).menuItemViewModels)
      .containsExactly(
        RecentRepository("/Projects/shopping-app/.git"),
        RecentRepository("/Projects/coffee/.git"),
        Separator,
        ClearRecent
      )
      .inOrder()
  }

  @Test
  fun `it should disable missing repository items from recent repositories menu`() {
    // given
    val missingRepository = "/Projects/coffee/.git"
    val useCase = OpenRecentMenuViewModelUseCase(repositoriesStorage) { it != missingRepository }

    val recentRepositories = listOf("/Projects/shopping-app/.git", missingRepository)
      .map(::RecentGitRepository)
    whenever(repositoriesStorage.getRecentRepositories())
      .thenReturn(recentRepositories)

    // when
    val openRecentMenuViewModel = useCase.invoke()

    // then
    assertThat(openRecentMenuViewModel)
      .isInstanceOf(NonEmpty::class.java)

    assertThat((openRecentMenuViewModel as NonEmpty).menuItemViewModels)
      .containsExactly(
        RecentRepository("/Projects/shopping-app/.git"),
        RecentRepository(missingRepository, false),
        Separator,
        ClearRecent
      )
      .inOrder()
  }
}
