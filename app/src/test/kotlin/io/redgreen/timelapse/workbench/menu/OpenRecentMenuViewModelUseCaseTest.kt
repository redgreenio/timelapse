package io.redgreen.timelapse.workbench.menu

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
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
      .isEqualTo(EmptyMenuViewModel)
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
      .isInstanceOf(NonEmptyMenuViewModel::class.java)

    assertThat((openRecentMenuViewModel as NonEmptyMenuViewModel).menuItemViewModels)
      .containsExactly(
        RecentRepositoryMenuItemViewModel(RecentGitRepository("/Projects/shopping-app/.git"), true),
        RecentRepositoryMenuItemViewModel(RecentGitRepository("/Projects/coffee/.git"), true),
        SeparatorMenuItemViewModel,
        ClearRecentMenuItemViewModel
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
      .isInstanceOf(NonEmptyMenuViewModel::class.java)

    assertThat((openRecentMenuViewModel as NonEmptyMenuViewModel).menuItemViewModels)
      .containsExactly(
        RecentRepositoryMenuItemViewModel(RecentGitRepository("/Projects/shopping-app/.git"), true),
        RecentRepositoryMenuItemViewModel(RecentGitRepository(missingRepository), false),
        SeparatorMenuItemViewModel,
        ClearRecentMenuItemViewModel
      )
      .inOrder()
  }
}
