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

  @Test
  fun `it should apply an empty patch on a non-empty text`() {
    // given
    val helloWorld = "Hello, world!"
    val patch = ""

    // when
    val patchedText = applyPatch(helloWorld, patch)

    // then
    assertThat(patchedText)
      .isEqualTo(helloWorld)
  }

  @Test
  fun `it should apply a deletion patch on a non-empty text to a non-empty text`() {
    // given
    val helloWorld = """
      Hello, world!
      Nice to meet you!
    """.trimIndent()

    val patch = """
      @@ -1,2 +1 @@
      -Hello, world!
       Nice to meet you!
    """.trimIndent()

    // when
    val patchedText = applyPatch(helloWorld, patch)

    // then
    assertThat(patchedText)
      .isEqualTo("Nice to meet you!")
  }

  @Test
  fun `it should apply a deletion patch on a non-empty text to an empty text`() {
    // given
    val originalText = "Nice to meet you!"

    val patch = """
      @@ -1 +0,0 @@
      -Nice to meet you!
    """.trimIndent()

    // when
    val patchedText = applyPatch(originalText, patch)

    // then
    assertThat(patchedText)
      .isEqualTo("")
  }
}
