package io.redgreen.timelapse.complexity

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.git.model.CommitHash
import io.redgreen.timelapse.git.model.Identity
import org.junit.jupiter.api.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

class ParserTest {
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
      ZonedDateTime.of(2021, 6, 20, 5, 38, 41, 0, ZoneOffset.of("+05:30")),
      Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
      ZonedDateTime.of(2021, 6, 20, 5, 38, 41, 0, ZoneOffset.of("+05:30")),
      "cli/src/main/kotlin/io/redgreen/fluid/cli/ui/Printer.kt",
      Stats(0, 7)
    )

    assertThat(actualCommit)
      .isEqualTo(expectedCommit)
  }
}
