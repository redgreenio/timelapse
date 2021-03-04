package io.redgreen.timelapse.core

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.fixtures.FixtureRepository.SimpleAndroid
import io.redgreen.timelapse.fixtures.GitTestbed
import java.io.File
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class GitDirectoryTest {
  companion object {
    @JvmStatic
    @Suppress("unused") // Used by JUnit parameterized test
    fun gitDirectories(): List<String> {
      return listOf(GitTestbed.path.absolutePath, SimpleAndroid.path.absolutePath)
        .map { "$it/.git" }
    }
  }

  @Test
  fun `it should return invalid for a non-existent directory path`() {
    // given
    val invalidPath = "/i/dont/exist"

    // when
    val gitDirectoryOptional = GitDirectory.from(invalidPath)

    // then
    assertThat(gitDirectoryOptional.isEmpty)
      .isTrue()
  }

  @Test
  fun `it should return invalid for a existing directory that is not a git directory`() {
    // given
    val workingDirectoryPath = File("").absolutePath

    // when
    val gitDirectoryOptional = GitDirectory.from(workingDirectoryPath)

    // then
    assertThat(gitDirectoryOptional.isEmpty)
      .isTrue()
  }

  @ParameterizedTest
  @MethodSource("gitDirectories")
  fun `it should return valid for existing git directories`(path: String) {
    // when
    val gitDirectoryOptional = GitDirectory.from(path)

    // then
    assertThat(gitDirectoryOptional.get().path)
      .isEqualTo(path)
  }
}
