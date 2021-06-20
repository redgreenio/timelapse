package io.redgreen.timelapse.complexity

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.git.model.CommitHash
import io.redgreen.timelapse.git.model.Identity
import org.junit.jupiter.api.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

class ParserTest {
  private val istOffset = ZoneOffset.of("+05:30")

  @Test
  fun `it should parse commit text with short commit message`() {
    // given
    val rawCommit = """
      cdf75934bad11807964253b0c74dac2f8d666be5 |@| Ragunath Jawahar |@| ragunath@redgreen.io |@| 2021-06-20T05:38:41+05:30
      Ragunath Jawahar |@| ragunath@redgreen.io |@| 2021-06-20T05:38:41+05:30
      refactor: rename classes
      
      0       7       cli/src/main/kotlin/io/redgreen/fluid/cli/ui/Printer.kt
    """.trimIndent()

    // when
    val actualCommit = parse(rawCommit)

    // then
    val expectedCommit = ParsedCommit(
      CommitHash("cdf75934bad11807964253b0c74dac2f8d666be5"),
      "refactor: rename classes",
      Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
      ZonedDateTime.of(2021, 6, 20, 5, 38, 41, 0, istOffset),
      Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
      ZonedDateTime.of(2021, 6, 20, 5, 38, 41, 0, istOffset),
      "cli/src/main/kotlin/io/redgreen/fluid/cli/ui/Printer.kt",
      Stats(0, 7)
    )

    assertThat(actualCommit)
      .isEqualTo(expectedCommit)
  }

  @Test
  fun `it should parse a list of commit text`() {
    // given
    val rawCommits = """
      cdf75934bad11807964253b0c74dac2f8d666be5 |@| Ragunath Jawahar |@| ragunath@redgreen.io |@| 2021-06-20T05:38:41+05:30
      Ragunath Jawahar |@| ragunath@redgreen.io |@| 2021-06-20T05:38:41+05:30
      refactor: rename classes

      0       7       cli/src/main/kotlin/io/redgreen/fluid/cli/ui/Printer.kt
      87e7d3e664d0e0a17d417903528a67641be66048 |@| Ragunath Jawahar |@| ragunath@redgreen.io |@| 2020-04-17T18:44:00+05:30
      Ragunath Jawahar |@| ragunath@redgreen.io |@| 2020-04-19T21:54:28+05:30
      feat: ask for user's confirmation before overwriting

      2       2       cli/src/main/kotlin/io/redgreen/fluid/cli/ui/Printer.kt
      e119c5d14a7634c2f8478e92c5bb40a88feff3d4 |@| Ragunath Jawahar |@| ragunath@redgreen.io |@| 2020-04-13T20:14:29+05:30
      Ragunath Jawahar |@| ragunath@redgreen.io |@| 2020-04-13T22:03:44+05:30
      feat: use the 'install' command to perform a fresh install

      7       0       cli/src/main/kotlin/io/redgreen/fluid/cli/ui/Printer.kt
    """.trimIndent()

    // when
    val commits = parseAll(rawCommits)

    // then
    assertThat(commits)
      .containsExactly(
        ParsedCommit(
          CommitHash("cdf75934bad11807964253b0c74dac2f8d666be5"),
          "refactor: rename classes",
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          ZonedDateTime.of(2021, 6, 20, 5, 38, 41, 0, istOffset),
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          ZonedDateTime.of(2021, 6, 20, 5, 38, 41, 0, istOffset),
          "cli/src/main/kotlin/io/redgreen/fluid/cli/ui/Printer.kt",
          Stats(0, 7)
        ),

        ParsedCommit(
          CommitHash("87e7d3e664d0e0a17d417903528a67641be66048"),
          "feat: ask for user's confirmation before overwriting",
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          ZonedDateTime.of(2020, 4, 17, 18, 44, 0, 0, istOffset),
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          ZonedDateTime.of(2020, 4, 19, 21, 54, 28, 0, istOffset),
          "cli/src/main/kotlin/io/redgreen/fluid/cli/ui/Printer.kt",
          Stats(2, 2)
        ),

        ParsedCommit(
          CommitHash("e119c5d14a7634c2f8478e92c5bb40a88feff3d4"),
          "feat: use the 'install' command to perform a fresh install",
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          ZonedDateTime.of(2020, 4, 13, 20, 14, 29, 0, istOffset),
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          ZonedDateTime.of(2020, 4, 13, 22, 3, 44, 0, istOffset),
          "cli/src/main/kotlin/io/redgreen/fluid/cli/ui/Printer.kt",
          Stats(7, 0)
        )
      )
  }

  @Test
  fun `it should parse a commit with a stats line containing tabs`() {
    val rawCommit = """
      cdf75934bad11807964253b0c74dac2f8d666be5 |@| Ragunath Jawahar |@| ragunath@redgreen.io |@| 2021-06-20T05:38:41+05:30
      Ragunath Jawahar |@| ragunath@redgreen.io |@| 2021-06-20T05:38:41+05:30
      refactor: rename classes
      
      0	207	app/src/main/java/org/simple/clinic/newentry/PatientEntryScreenController.kt
    """.trimIndent()

    // when
    val actualCommit = parse(rawCommit)

    // then
    val expectedCommit = ParsedCommit(
      CommitHash("cdf75934bad11807964253b0c74dac2f8d666be5"),
      "refactor: rename classes",
      Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
      ZonedDateTime.of(2021, 6, 20, 5, 38, 41, 0, istOffset),
      Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
      ZonedDateTime.of(2021, 6, 20, 5, 38, 41, 0, istOffset),
      "app/src/main/java/org/simple/clinic/newentry/PatientEntryScreenController.kt",
      Stats(0, 207)
    )

    assertThat(actualCommit)
      .isEqualTo(expectedCommit)
  }

  @Test
  fun `it should parse a commit with a renamed file`() {
    // given
    val rawCommit = """
      b065618bf24c7876a7f4e165c41e7df2bd396731 |@| John Doe |@| john@somewhere.in |@| 2018-06-15T20:02:04+05:30
      Hans Solo |@| hans@somewhere.in |@| 2018-06-15T20:57:38+05:30
      Change package name to org.simple.clinic

      15	15	app/src/main/java/org/{resolvetosavelives/red => simple/clinic}/newentry/PatientEntryScreenController.kt
    """.trimIndent()

    // when
    val actualCommit = parse(rawCommit)

    // then
    val expectedCommit = ParsedCommit(
      CommitHash("b065618bf24c7876a7f4e165c41e7df2bd396731"),
      "Change package name to org.simple.clinic",
      Identity("John Doe", "john@somewhere.in"),
      ZonedDateTime.of(2018, 6, 15, 20, 2, 4, 0, istOffset),
      Identity("Hans Solo", "hans@somewhere.in"),
      ZonedDateTime.of(2018, 6, 15, 20, 57, 38, 0, istOffset),
      "app/src/main/java/org/simple/clinic/newentry/PatientEntryScreenController.kt",
      Stats(15, 15)
    )

    assertThat(actualCommit)
      .isEqualTo(expectedCommit)
  }
}
