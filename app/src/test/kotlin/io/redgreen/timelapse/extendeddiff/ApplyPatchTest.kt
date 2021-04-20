package io.redgreen.timelapse.extendeddiff

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ApplyPatchTest {
  @Test
  fun `it should apply a patch on an empty text`() {
    // given
    val emptyText = """
      |
    """.trimMargin("|")

    val patch = """
      @@ -0,0 +1 @@
      +Hello, world!
    """.trimIndent()

    // when
    val patchedText = applyPatch(emptyText, patch)

    // then
    assertThat(patchedText)
      .isEqualTo(
        """
          Hello, world!
        """.trimIndent()
      )
  }

  @Test
  fun `it should apply a patch on an non-empty text`() {
    // given
    val helloWorldText = """
      Hello, world!
    """.trimIndent()

    val patch = """
      @@ -1 +1,2 @@
      -Hello, world!
      +Hello, world!
      +Nice to meet you!
    """.trimIndent()

    // when
    val patchedText = applyPatch(helloWorldText, patch)

    // then
    assertThat(patchedText)
      .isEqualTo(
        """
          Hello, world!
          Nice to meet you!
        """.trimIndent()
      )
  }

  @Test
  fun `it should apply an empty patch on an empty text`() {
    // given
    val emptyText = ""
    val patch = ""

    // when
    val patchedText = applyPatch(emptyText, patch)

    // then
    assertThat(patchedText)
      .isEqualTo(emptyText)
  }
}
