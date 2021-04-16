package io.redgreen.timelapse.gradle.automatedrelease

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.gradle.automatedrelease.versions.NoPreviousVersion
import io.redgreen.timelapse.gradle.automatedrelease.versions.InternalVersion
import io.redgreen.timelapse.gradle.automatedrelease.versions.ReleaseVersion
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
        "2021.1" to "2021.2",
        "2021.2" to "2021.3",
        "2019.665" to "2019.666" // covid
      )
    }

    @JvmStatic
    fun predecessorInternalReleasesAndNextInternalReleases(): List<Pair<String, String>> {
      return listOf(
        "2021.6.1" to "2021.6.2",
        "2020.1.3" to "2020.1.4",
        "2020.0.5" to "2020.0.6"
      )
    }

    @JvmStatic
    fun predecessorInternalReleasesAndNextPublicReleases(): List<Pair<String, String>> {
      return listOf(
        "2021.5.12" to "2021.6",
        "2020.1.3" to "2020.2",
        "2021.0.7" to "2021.1"
      )
    }

    @JvmStatic
    fun yearWithNoPublicReleasesAndNextInternalReleases(): List<Pair<Int, String>> {
      return listOf(
        2021 to "2021.0.1",
        2022 to "2022.0.1"
      )
    }

    @JvmStatic
    fun displayTextsAndInstanceTypes(): List<Pair<String, Class<out Version>>> {
      return listOf(
        "2021" to NoPreviousVersion::class.java,
        "2021.1" to ReleaseVersion::class.java,
        "2021.1.1" to InternalVersion::class.java
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
  @MethodSource("yearWithNoPublicReleasesAndNextInternalReleases")
  fun `it should give the first internal release version for a year with no releases`(
    yearWithNoPublicReleaseAndNextInternalRelease: Pair<Int, String>
  ) {
    // given
    val (yyyy, nextInternalRelease) = yearWithNoPublicReleaseAndNextInternalRelease

    // when
    val internalVersionWithNoPublicReleases = Versioning.getNextVersion(yyyy, "", false)

    // then
    assertThat(internalVersionWithNoPublicReleases)
      .isEqualTo(nextInternalRelease)
  }

  @Test
  internal fun `it should get the next public version from the current public version`() {
    // given
    val publicRelease = ReleaseVersion(2021, 1)

    // when
    val nextPublicRelease = publicRelease.public()

    // then
    assertThat(nextPublicRelease.displayText)
      .isEqualTo("2021.2")
  }

  @Test
  internal fun `it should get the next internal version from the current public version`() {
    // given
    val publicRelease = ReleaseVersion(2021, 1)

    // when
    val nextInternalRelease = publicRelease.internal()

    // then
    assertThat(nextInternalRelease.displayText)
      .isEqualTo("2021.1.1")
  }

  @ParameterizedTest
  @MethodSource("displayTextsAndInstanceTypes")
  fun `it should return the correct instance based on the display text`(
    displayTextAndInstanceType: Pair<String, Class<out Version>>
  ) {
    // given
    val (displayText, instanceType) = displayTextAndInstanceType

    // when
    val version = Version.from(displayText)

    // then
    assertThat(version::class.java)
      .isEqualTo(instanceType)
  }
}
