package io.redgreen.timelapse.gradle.automatedrelease

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.gradle.automatedrelease.versions.InternalVersion
import io.redgreen.timelapse.gradle.automatedrelease.versions.NoPreviousVersion
import io.redgreen.timelapse.gradle.automatedrelease.versions.PublicReleaseVersion
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

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
        "2021.1" to PublicReleaseVersion::class.java,
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
    val nextVersion = Versioning.getNextVersion("$year")

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
    val nextVersion = Versioning.getNextVersion(predecessor)

    // then
    assertThat(nextVersion)
      .isEqualTo(nextRelease)
  }

  @Test
  fun `it should get the internal release for the given public release`() {
    // given
    val publicReleaseVersion = "2021.5"

    // when
    val nextInternalVersion = Versioning.getNextVersion(publicReleaseVersion, false)

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
    val nextInternalVersion = Versioning.getNextVersion(predecessorInternalVersion, false)

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
    val publicReleaseVersion = Versioning.getNextVersion(predecessorInternalRelease, true)

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
    val internalVersionWithNoPublicReleases = Versioning.getNextVersion("$yyyy", false)

    // then
    assertThat(internalVersionWithNoPublicReleases)
      .isEqualTo(nextInternalRelease)
  }

  @Test
  internal fun `it should get the next public version from the current public version`() {
    // given
    val publicRelease = PublicReleaseVersion(2021, 1)

    // when
    val nextPublicRelease = publicRelease.publicRelease()

    // then
    assertThat(nextPublicRelease.displayText)
      .isEqualTo("2021.2")
  }

  @Test
  internal fun `it should get the next internal version from the current public version`() {
    // given
    val publicRelease = PublicReleaseVersion(2021, 1)

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
    val version = VersionDeserializer.deserialize(displayText)

    // then
    assertThat(version::class.java)
      .isEqualTo(instanceType)
  }

  @ParameterizedTest
  @ValueSource(ints = [2021, 2022])
  fun `it should return the current year for an empty list of tags`(yyyy: Int) {
    // given
    val tags = listOf<String>()

    // when
    val tag = Versioning.getLatestTag(tags, yyyy)

    // then
    assertThat(tag)
      .isEqualTo("$yyyy")
  }

  @Test
  fun `it should return the current year when the tag list does not contain any releases this year`() {
    // given
    val tagsFromPreviousYear = listOf("2020.0.1", "2020.0.2", "2020.1", "2020.2")

    // when
    val latestTag = Versioning.getLatestTag(tagsFromPreviousYear, 2021)

    // then
    assertThat(latestTag)
      .isEqualTo("2021")
  }

  @Test
  fun `it should return the current year when the tag list contains non-conforming tags`() {
    // given
    val nonConformingTags = listOf("0.0.1", "0.0.2", "0.1.0", "0.1.1", "0.1.2")

    // when
    val latestTag = Versioning.getLatestTag(nonConformingTags, 2021)

    // then
    assertThat(latestTag)
      .isEqualTo("2021")
  }

  @Test
  fun `it should return the latest tag (public) for the current year`() {
    // given
    val tagsFromPreviousYear = listOf(
      "2020.0.1", "2020.0.2", "2021.1", "2021.2", "0.0.1", "0.0.2", "0.1.0", "0.1.1", "0.1.2"
    )

    // when
    val latestTag = Versioning.getLatestTag(tagsFromPreviousYear, 2021)

    // then
    assertThat(latestTag)
      .isEqualTo("2021.2")
  }

  @Test
  fun `it should return the latest tag (internal) for the current year`() {
    // given
    val tagsFromPreviousYear = listOf(
      "2020.0.1", "2020.0.2", "2021.1", "2021.2", "2021.2.1", "2021.2.2", "0.0.1", "0.0.2", "0.1.0", "0.1.1", "0.1.2"
    )

    // when
    val latestTag = Versioning.getLatestTag(tagsFromPreviousYear, 2021)

    // then
    assertThat(latestTag)
      .isEqualTo("2021.2.2")
  }
}
