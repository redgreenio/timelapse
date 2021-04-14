package io.redgreen.timelapse.gradle.automatedrelease

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class VersioningTest {
  companion object {
    @JvmStatic
    fun yearsAndFirstReleases(): List<Pair<Int, String>> {
      return listOf(
        2019 to "2019.1",
        2021 to "2021.1",
        2022 to "2022.1",
        1999 to "1999.1" // Y2K
      )
    }

    @JvmStatic
    fun predecessorsAndNextReleases(): List<Pair<String, String>> {
      return listOf(
        "2021.1" to "2021.2"
      )
    }
  }

  @ParameterizedTest
  @MethodSource("yearsAndFirstReleases")
  fun `it should get the first public release of the given year`(yearAndFirstRelease: Pair<Int, String>) {
    // given
    val (year, firstRelease) = yearAndFirstRelease

    // when
    val nextVersion = Versioning.getNextVersion(year, "")

    // then
    assertThat(nextVersion)
      .isEqualTo(firstRelease)
  }

  @ParameterizedTest
  @MethodSource("predecessorsAndNextReleases")
  fun `it should get the next version for the predecessor version`(predecessorAndNextRelease: Pair<String, String>) {
    // given
    val (predecessor, nextRelease) = predecessorAndNextRelease

    // when
    val nextVersion = Versioning.getNextVersion(2021, predecessor)

    // then
    assertThat(nextVersion)
      .isEqualTo(nextRelease)
  }
}
