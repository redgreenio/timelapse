package io.redgreen.truth.extensions.javafx

import com.google.common.truth.ExpectFailure.SimpleSubjectBuilderCallback
import com.google.common.truth.ExpectFailure.expectFailureAbout
import io.redgreen.truth.extensions.javafx.MenuItemSubject.Companion.assertThat
import io.redgreen.truth.extensions.javafx.MenuItemSubject.Companion.menuItems
import javafx.scene.control.MenuItem
import org.junit.jupiter.api.Test

class MenuItemSubjectTest {
  companion object {
    private const val LAUNCH = "Launch"

    fun expectFailure(
      callback: SimpleSubjectBuilderCallback<MenuItemSubject, MenuItem>
    ): AssertionError {
      return expectFailureAbout(menuItems(), callback)
    }
  }

  private val launchMenuItem = MenuItem(LAUNCH)

  @Test
  fun `menu item text`() {
    assertThat(launchMenuItem)
      .hasText(LAUNCH)

    expectFailure { whenTesting ->
      whenTesting.that(launchMenuItem).hasText("Hold")
    }
  }

  @Test
  fun `menu item enabled`() {
    assertThat(launchMenuItem)
      .isEnabled()
  }

  @Test
  fun `menu item disabled`() {
    val disabledLaunchMenuItem = launchMenuItem.apply {
      isDisable = true
    }

    assertThat(disabledLaunchMenuItem)
      .isDisabled()
  }
}
