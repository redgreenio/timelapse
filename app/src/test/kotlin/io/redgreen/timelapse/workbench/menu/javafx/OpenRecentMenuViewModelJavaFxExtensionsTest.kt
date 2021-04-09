package io.redgreen.timelapse.workbench.menu.javafx

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemViewModel.ClearRecent
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemViewModel.RecentRepository
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.Empty
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.NonEmpty
import org.junit.jupiter.api.Test

class OpenRecentMenuViewModelJavaFxExtensionsTest {
  @Test
  fun `it should create a disabled JavaFx open recent menu when there are not recent repositories`() {
    // given & when
    val javaFxEmptyOpenRecentMenu = Empty.toJavaFxMenu()

    // then
    assertThat(javaFxEmptyOpenRecentMenu.text)
      .isEqualTo("Open Recent")
    assertThat(javaFxEmptyOpenRecentMenu.isDisable)
      .isTrue()
  }

  @Test
  fun `it should create an enabled JavaFx open recent menu with recent repositories and a clear menu item`() {
    // given
    val shoppingAppRepository = "/Projects/shopping-app/.git"
    val coffeeRepository = "/Projects/coffee/.git"
    val openRecentMenuItemViewModels = listOf(shoppingAppRepository, coffeeRepository)
      .map(::RecentRepository) + ClearRecent

    // when
    val javaFxNonEmptyOpenRecentMenu = NonEmpty(openRecentMenuItemViewModels)
      .toJavaFxMenu()

    // then
    assertThat(javaFxNonEmptyOpenRecentMenu.text)
      .isEqualTo("Open Recent")
    assertThat(javaFxNonEmptyOpenRecentMenu.isDisable)
      .isFalse()

    val recentMenuItemProperties = javaFxNonEmptyOpenRecentMenu
      .items
      .map { it.text to !it.isDisable }
    assertThat(recentMenuItemProperties)
      .containsExactly(
        shoppingAppRepository to true,
        coffeeRepository to true,
        "Clear Recent" to true
      )
      .inOrder()
  }
}
