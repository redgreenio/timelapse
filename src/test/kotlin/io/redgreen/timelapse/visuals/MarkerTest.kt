package io.redgreen.timelapse.visuals

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.visuals.DiffLine.Marker
import org.junit.jupiter.api.Test

class MarkerTest {
  @Test
  fun `it should compute old and new start lines for a modified file`() {
    val marker = Marker("@@ -1,6 +1,6 @@")

    assertThat(marker.oldLineNumber)
      .isEqualTo(1)
    assertThat(marker.newLineNumber)
      .isEqualTo(1)
  }

  @Test
  fun `it should compute old and new start lines for a deleted file`() {
    val marker = Marker("@@ -1 +0,0 @@")

    assertThat(marker.oldLineNumber)
      .isEqualTo(1)
    assertThat(marker.newLineNumber)
      .isEqualTo(0)
  }

  @Test
  fun `it should compute old and new start lines for a new file`() {
    val marker = Marker("@@ -0,0 +1 @@")

    assertThat(marker.oldLineNumber)
      .isEqualTo(0)
    assertThat(marker.newLineNumber)
      .isEqualTo(1)
  }
}
