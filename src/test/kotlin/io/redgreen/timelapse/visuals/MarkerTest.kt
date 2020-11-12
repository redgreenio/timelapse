package io.redgreen.timelapse.visuals

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.visuals.DiffLine.Marker
import org.junit.jupiter.api.Test

class MarkerTest {
  @Test
  fun `it should give us the old start line`() {
    val marker = Marker("@@ -1,6 +1,6 @@")

    assertThat(marker.oldLineNumber)
      .isEqualTo(1)
    assertThat(marker.newLineNumber)
      .isEqualTo(1)
  }
}
