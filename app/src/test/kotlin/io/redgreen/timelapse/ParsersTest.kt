package io.redgreen.timelapse

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.domain.Change.Modification
import io.redgreen.timelapse.domain.Change.Move
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
    val changes = parseGitFollowOutput(commitSummaryBlock)

    // then
    val expectedChange = Modification("f5a1ed7", "Add canary test targeting Java 9", 25)
    assertThat(changes)
      .containsExactly(expectedChange)
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
    val changes = parseGitFollowOutput(commitSummaryBlock)

    // then
    val expectedChange = Modification("986c5ff", "Get rid of root project and optimize gradle buildscripts", 8, 22)
    assertThat(changes)
      .containsExactly(expectedChange)
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
    val changes = parseGitFollowOutput(commitSummaryBlock)

    // then
    val expectedChange = Modification("8eca717", "Remove stale comments", deletions = 2)
    assertThat(changes)
      .containsExactly(expectedChange)
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
    val changes = parseGitFollowOutput(commitSummaryBlock)

    // then
    val expectedChange = Modification("b35d60f", "Bump up dependencies for `server` module", 1, 1)
    assertThat(changes)
      .containsExactly(expectedChange)
  }

  @Test
  fun `it should parse multiple git summary blocks from the output`() {
    // given
    val commitSummaryBlocks = """
      79c6217 Add an Android SDK module
       build.gradle | 14 ++++++++++++++
       1 file changed, 14 insertions(+)
      986c5ff Get rid of root project and optimize gradle buildscripts
       build.gradle | 30 ++++++++----------------------
       1 file changed, 8 insertions(+), 22 deletions(-)
      f5a1ed7 Add canary test targeting Java 9
       build.gradle | 25 +++++++++++++++++++++++++
       1 file changed, 25 insertions(+)
    """.trimIndent()

    // when
    val changes = parseGitFollowOutput(commitSummaryBlocks)

    // then
    assertThat(changes)
      .containsExactly(
        Modification("79c6217", "Add an Android SDK module", 14),
        Modification("986c5ff", "Get rid of root project and optimize gradle buildscripts", 8, 22),
        Modification("f5a1ed7", "Add canary test targeting Java 9", 25),
      )
      .inOrder()
  }

  @Test
  fun `it should parse summary block with branch information`() {
    // given
    val commitSummaryBlock = """
      77bc5c1 (HEAD -> develop) chore: update Kotlin version
       build.gradle | 2 +-
       1 file changed, 1 insertion(+), 1 deletion(-)
    """.trimIndent()

    // when
    val changes = parseGitFollowOutput(commitSummaryBlock)

    // then
    assertThat(changes)
      .containsExactly(
        Modification("77bc5c1", "chore: update Kotlin version", 1, 1)
      )
  }

  @Test
  fun `it should parse summary block with a move change`() {
    // given
    val fileMoveCommitSummaryBlock = """
      a30f50994 Move OngoingEditPatientEntry to the patient package
       .../clinic/{editpatient => patient}/OngoingEditPatientEntry.kt       | 5 ++---
       1 file changed, 2 insertions(+), 3 deletions(-)
    """.trimIndent()

    // when
    val changes = parseGitFollowOutput(fileMoveCommitSummaryBlock)

    // then
    val oldPathFragment = "/clinic/editpatient/OngoingEditPatientEntry.kt"
    val newPathFragment = "/clinic/patient/OngoingEditPatientEntry.kt"
    assertThat(changes)
      .containsExactly(
        Move("a30f50994", "Move OngoingEditPatientEntry to the patient package", 2, 3, oldPathFragment, newPathFragment)
      )
  }

  @Test
  fun `it should parse summary block with a rename change`() {
    // given
    val fileMoveCommitSummaryBlock = """
      151de7ea4 Rename PatientData -> OngoingEditPatientEntry
       .../clinic/editpatient/{PatientData.kt => OngoingEditPatientEntry.kt}   | 2 +-
       1 file changed, 1 insertion(+), 1 deletion(-)
    """.trimIndent()

    // when
    val changes = parseGitFollowOutput(fileMoveCommitSummaryBlock)

    // then
    val oldPathFragment = "/clinic/editpatient/PatientData.kt"
    val newPathFragment = "/clinic/editpatient/OngoingEditPatientEntry.kt"
    assertThat(changes)
      .containsExactly(
        Move("151de7ea4", "Rename PatientData -> OngoingEditPatientEntry", 1, 1, oldPathFragment, newPathFragment)
      )
  }
}
