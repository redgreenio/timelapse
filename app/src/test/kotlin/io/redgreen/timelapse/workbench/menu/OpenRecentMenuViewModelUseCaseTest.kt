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

  private val shoppingAppRecentRepository = RecentGitRepository("/Projects/shopping-app/.git")
  private val coffeeRecentRepository = RecentGitRepository("/Projects/coffee/.git")

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
    val recentRepositories = listOf(shoppingAppRecentRepository, coffeeRecentRepository)
    whenever(repositoriesStorage.getRecentRepositories())
      .thenReturn(recentRepositories)

    // when
    val openRecentMenuViewModel = useCase.invoke()

    // then
    assertThat(openRecentMenuViewModel)
      .isInstanceOf(NonEmptyMenuViewModel::class.java)

    assertThat((openRecentMenuViewModel as NonEmptyMenuViewModel).menuItemViewModels)
      .containsExactly(
        RecentRepositoryMenuItemViewModel(shoppingAppRecentRepository),
        RecentRepositoryMenuItemViewModel(coffeeRecentRepository),
        SeparatorMenuItemViewModel,
        ClearRecentMenuItemViewModel
      )
      .inOrder()
  }

  @Test
  fun `it should disable missing repository items from recent repositories menu`() {
    // given
    val useCase = OpenRecentMenuViewModelUseCase(repositoriesStorage) { it != coffeeRecentRepository.path }

    val recentRepositories = listOf(shoppingAppRecentRepository, coffeeRecentRepository)
    whenever(repositoriesStorage.getRecentRepositories())
      .thenReturn(recentRepositories)

    // when
    val openRecentMenuViewModel = useCase.invoke()

    // then
    assertThat(openRecentMenuViewModel)
      .isInstanceOf(NonEmptyMenuViewModel::class.java)

    assertThat((openRecentMenuViewModel as NonEmptyMenuViewModel).menuItemViewModels)
      .containsExactly(
        RecentRepositoryMenuItemViewModel(shoppingAppRecentRepository),
        RecentRepositoryMenuItemViewModel(coffeeRecentRepository, false),
        SeparatorMenuItemViewModel,
        ClearRecentMenuItemViewModel
      )
      .inOrder()
  }
}
