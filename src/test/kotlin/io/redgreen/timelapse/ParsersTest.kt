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
    val expectedChange = Change("f5a1ed7", "Add canary test targeting Java 9", 25)
    assertThat(change)
      .isEqualTo(expectedChange)
  }

  @Test
  fun `it should parse a git follow summary block with insertions and deletions`() {
    // given
    val commitSummaryBlock = """
      986c5ff Get rid of root project and optimize gradle buildscripts
       build.gradle | 30 ++++++++----------------------
       1 file changed, 8 insertions(+), 22 deletions(-)
    """.trimIndent()

    // when
    val change = parseGitFollowOutput(commitSummaryBlock)

    // then
    val expectedChange = Change("986c5ff", "Get rid of root project and optimize gradle buildscripts", 8, 22)
    assertThat(change)
      .isEqualTo(expectedChange)
  }

  @Test
  fun `it should parse a git follow summary block with deletions`() {
    // given
    val commitSummaryBlock = """
      8eca717 Remove stale comments
       build.gradle | 2 --
       1 file changed, 2 deletions(-)
    """.trimIndent()

    // when
    val change = parseGitFollowOutput(commitSummaryBlock)

    // then
    val expectedChange = Change("8eca717", "Remove stale comments", deletions = 2)
    assertThat(change)
      .isEqualTo(expectedChange)
  }

  @Test
  fun `it should parse a git follow summary block with 1 insertion and 1 deletion`() {
    // given
    val commitSummaryBlock = """
      b35d60f Bump up dependencies for `server` module
       build.gradle | 2 +-
       1 file changed, 1 insertion(+), 1 deletion(-)
    """.trimIndent()

    // when
    val change = parseGitFollowOutput(commitSummaryBlock)

    // then
    val expectedChange = Change("b35d60f", "Bump up dependencies for `server` module", 1, 1)
    assertThat(change)
      .isEqualTo(expectedChange)
  }
}
