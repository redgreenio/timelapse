package io.redgreen.timelapse

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.domain.Change
import io.redgreen.timelapse.domain.parseGitFollowOutput
import org.junit.jupiter.api.Test

class ParsersTest {
  @Test
  fun `it should parse a git follow summary block with insertions`() {
    // given
    val commitSummaryBlock = """
      f5a1ed7 Add canary test targeting Java 9
       build.gradle | 25 +++++++++++++++++++++++++
       1 file changed, 25 insertions(+)
    """.trimIndent()

    // when
    val change = parseGitFollowOutput(commitSummaryBlock)

    // then
    assertThat(change)
      .isEqualTo(Change("f5a1ed7", "Add canary test targeting Java 9", 25))
  }
}
