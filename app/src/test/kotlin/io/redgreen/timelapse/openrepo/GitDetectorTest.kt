package io.redgreen.timelapse.openrepo

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.fixtures.SimpleAndroid
import org.junit.jupiter.api.Test

class GitDetectorTest {
  private val gitDetector = GitDetector()

  @Test
  fun `it should return false if a path is invalid`() {
    // given
    val nonExistentPath = "/non/existent/directory"

    // when
    val isGitRepository = gitDetector.isGitRepository(nonExistentPath)

    // then
    assertThat(isGitRepository)
      .isFalse()
  }

  @Test
  fun `it should return false if the path does not contain a git directory`() {
    // given
    val notGitDirectoryPath = "../tools"

    // when
    val isGitRepository = gitDetector.isGitRepository(notGitDirectoryPath)

    // then
    assertThat(isGitRepository)
      .isFalse()
  }

  @Test
  fun `it should return true if the path contains a valid git directory`() {
    // given
    val gitDirectoryPath = SimpleAndroid.path.path

    // when
    val isGitRepository = gitDetector.isGitRepository(gitDirectoryPath)

    // then
    assertThat(isGitRepository)
      .isTrue()
  }
}
