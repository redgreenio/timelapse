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

  @Test
  fun `it should get affected line numbers for side B (non-empty A, starting from 1)`() {
    // given
    val unifiedPatch = """
      --- a.txt	2021-05-17 05:00:42.000000000 +0530
      +++ b.txt	2021-05-17 05:00:56.000000000 +0530
      @@ -1 +1,2 @@
      -Hello, world!
      \ No newline at end of file
      +Hello, world!
      +Enjoy enjaami!
      \ No newline at end of file
    """.trimIndent()
    val patchFile = PatchFile.from(unifiedPatch)

    // when
    val affectedLineNumbers = patchFile.getAffectedLineNumbers()

    // then
    assertThat(affectedLineNumbers)
      .containsExactly(1, 2)
  }

  @Test
  fun `it should get affected line numbers for side B (non-empty A, not starting from 1)`() {
    // given
    val unifiedPatch = """
      --- a.txt	2021-05-17 05:27:03.000000000 +0530
      +++ b.txt	2021-05-17 05:27:15.000000000 +0530
      @@ -1,3 +1,5 @@
      Two roads diverged in a yellow wood,
      And sorry I could not travel both
      -And be one traveler, long I stood
      \ No newline at end of file
      +And be one traveler, long I stood
      +And looked down one as far as I could
      +To where it bent in the undergrowth;
      \ No newline at end of file
    """.trimIndent()
    val patchFile = PatchFile.from(unifiedPatch)

    // when
    val affectedLineNumbers = patchFile.getAffectedLineNumbers()

    // then
    assertThat(affectedLineNumbers)
      .containsExactly(3, 4, 5)
  }
}
