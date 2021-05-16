package io.redgreen.timelapse.git.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

internal class PatchFileTest {
  @Test
  fun `it should get affected line numbers for side B (empty A, starting from 1)`() {
    // given
    val unifiedPatch = """
      --- a.txt	2021-05-17 04:54:53.000000000 +0530
      +++ b.txt	2021-05-17 04:55:00.000000000 +0530
      @@ -0,0 +1 @@
      +Hello, world!
      \ No newline at end of file
    """.trimIndent()
    val patchFile = PatchFile.from(unifiedPatch)

    // when
    val affectedLineNumbers = patchFile.getAffectedLineNumbers()

    // then
    assertThat(affectedLineNumbers)
      .containsExactly(1)
  }
}
