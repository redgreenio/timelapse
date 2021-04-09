package io.redgreen.timelapse.workbench.menu

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.Empty
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
}
