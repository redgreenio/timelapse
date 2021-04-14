package io.redgreen.timelapse.gradle.automatedrelease

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.api.Test

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
        "2021.1" to "2021.2",
        "2021.2" to "2021.3",
        "2019.665" to "2019.666" // covid
      )
    }

    @JvmStatic
    fun predecessorInternalReleasesAndNextInternalReleases(): List<Pair<String, String>> {
      return listOf(
        "2021.6.1" to "2021.6.2",
        "2020.1.3" to "2020.1.4"
      )
    }

    @JvmStatic
    fun predecessorInternalReleasesAndNextPublicReleases(): List<Pair<String, String>> {
      return listOf(
        "2021.5.12" to "2021.6",
        "2020.1.3" to "2020.2"
      )
    }

    @JvmStatic
    fun noPredecessorPublicReleasesAndNextInternalReleases(): List<Pair<String, String>> {
      return listOf(
        "" to "2021.0.1"
      )
    }
  }

  @ParameterizedTest
  @MethodSource("yearsAndFirstReleases")
  fun `it should get the first public release of the given year`(
    yearAndFirstRelease: Pair<Int, String>
  ) {
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
  fun `it should get the next public release version for the predecessor release version`(
    predecessorAndNextRelease: Pair<String, String>
  ) {
    // given
    val (predecessor, nextRelease) = predecessorAndNextRelease

    // when
    val nextVersion = Versioning.getNextVersion(2021, predecessor)

    // then
    assertThat(nextVersion)
      .isEqualTo(nextRelease)
  }

  @Test
  fun `it should get the internal release for the given public release`() {
    // given
    val publicReleaseVersion = "2021.5"

    // when
    val nextInternalVersion = Versioning.getNextVersion(2021, publicReleaseVersion, false)

    // then
    assertThat(nextInternalVersion)
      .isEqualTo("2021.5.1")
  }

  @ParameterizedTest
  @MethodSource("predecessorInternalReleasesAndNextInternalReleases")
  fun `it should get the next internal release for the given internal release`(
    predecessorInternalReleaseAndNextInternalRelease: Pair<String, String>
  ) {
    // given
    val (predecessorInternalVersion, expectedNextInternalVersion) = predecessorInternalReleaseAndNextInternalRelease

    // when
    val nextInternalVersion = Versioning.getNextVersion(2021, predecessorInternalVersion, false)

    // then
    assertThat(nextInternalVersion)
      .isEqualTo(expectedNextInternalVersion)
  }

  @ParameterizedTest
  @MethodSource("predecessorInternalReleasesAndNextPublicReleases")
  fun `it should get the next public release for the given internal release`(
    predecessorInternalReleaseAndNextPublicRelease: Pair<String, String>
  ) {
    // given
    val (predecessorInternalRelease, expectedNextPublicRelease) = predecessorInternalReleaseAndNextPublicRelease

    // when
    val publicReleaseVersion = Versioning.getNextVersion(2021, predecessorInternalRelease, true)

    // then
    assertThat(publicReleaseVersion)
      .isEqualTo(expectedNextPublicRelease)
  }

  @ParameterizedTest
  @MethodSource("noPredecessorPublicReleasesAndNextInternalReleases")
  fun `it should give the first internal release version for a year with no releases`(
    noPredecessorPublicReleaseAndNextInternalRelease: Pair<String, String>
  ) {
    // given
    val (noPredecessorPublicRelease, nextInternalRelease) = noPredecessorPublicReleaseAndNextInternalRelease

    // when
    val internalVersionWithNoPublicReleases = Versioning.getNextVersion(2021, noPredecessorPublicRelease, false)

    // then
    assertThat(internalVersionWithNoPublicReleases)
      .isEqualTo(nextInternalRelease)
  }
}
