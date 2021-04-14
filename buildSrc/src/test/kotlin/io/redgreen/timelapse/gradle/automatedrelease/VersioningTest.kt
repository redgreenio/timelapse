package io.redgreen.timelapse.gradle.automatedrelease

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class VersioningTest {
  @Test
  fun `it should get the first public release of the current year`() {
    // given & when
    val releaseVersion = Versioning.getReleaseVersion()

    // then
    assertThat(releaseVersion)
      .isEqualTo("2021.1")
  }
}
