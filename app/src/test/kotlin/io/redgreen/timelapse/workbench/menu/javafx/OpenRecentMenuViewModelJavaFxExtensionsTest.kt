package io.redgreen.timelapse.workbench.menu.javafx

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemViewModel.ClearRecent
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemViewModel.RecentRepository
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.Empty
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.NonEmpty
import io.redgreen.truth.extensions.javafx.MenuItemSubject.Companion.assertThat
import org.junit.jupiter.api.Test

class OpenRecentMenuViewModelJavaFxExtensionsTest {
  @Test
  fun `it should create a disabled JavaFx open recent menu when there are not recent repositories`() {
    // given & when
    val disabledOpenRecentJavaFxMenu = Empty.toJavaFxMenu()

    // then
    assertThat(disabledOpenRecentJavaFxMenu.text)
      .isEqualTo("Open Recent")
    assertThat(disabledOpenRecentJavaFxMenu.isDisable)
      .isTrue()
  }

  @Test
  fun `it should create an enabled JavaFx open recent menu with recent repositories and a clear menu item`() {
    // given
    val recentRepositoryMenuItemViewModels = listOf(
      "/Projects/shopping-app/.git",
      "/Projects/coffee/.git"
    )
      .map(::RecentRepository)
    val openRecentMenuItemViewModels = recentRepositoryMenuItemViewModels + ClearRecent

    // when
    val openRecentJavaFxMenu = NonEmpty(openRecentMenuItemViewModels)
      .toJavaFxMenu()

    // then
    assertThat(openRecentJavaFxMenu.text)
      .isEqualTo("Open Recent")
    assertThat(openRecentJavaFxMenu.isDisable)
      .isFalse()

    assertThat(openRecentJavaFxMenu.items)
      .hasSize(3)

    val (repositoryMenuItem1, repositoryMenuItem2, clearMenuItem) = openRecentJavaFxMenu.items

    assertThat(repositoryMenuItem1)
      .hasText("/Projects/shopping-app/.git")
      .isEnabled()

    assertThat(repositoryMenuItem2)
      .hasText("/Projects/coffee/.git")
      .isEnabled()

    assertThat(clearMenuItem)
      .hasText("Clear Recent")
      .isEnabled()
  }
}
