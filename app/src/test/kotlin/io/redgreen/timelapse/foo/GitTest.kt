package io.redgreen.timelapse.foo

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GitTest {
  @ParameterizedTest
  @ValueSource(
    strings = [
      "/Users/ajay/legal-ai",
      "/Users/ajay/legal-ai/",
    ]
  )
  @DisabledOnOs(OS.WINDOWS)
  fun `it should append dot git to a given path`(workingDirectoryPath: String) {
    // when
    val gitDirectoryPath = workingDirectoryPath.appendDotGit()

    // then
    assertThat(gitDirectoryPath)
      .isEqualTo("/Users/ajay/legal-ai/.git")
  }

  @ParameterizedTest
  @ValueSource(
    strings = [
      """D:\ajay\legal-ai""",
      """D:\ajay\legal-ai\""",
    ]
  )
  @EnabledOnOs(OS.WINDOWS)
  fun `it should append dot git to a given path (windows)`(workingDirectoryPath: String) {
    // when
    val gitDirectoryPath = workingDirectoryPath.appendDotGit()

    // then
    assertThat(gitDirectoryPath)
      .isEqualTo("""D:\ajay\legal-ai\.git""")
  }
}
