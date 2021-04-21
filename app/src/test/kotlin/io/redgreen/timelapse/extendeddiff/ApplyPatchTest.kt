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
      \ No newline at end of file
      +Hello, world!
      +Nice to meet you!
      \ No newline at end of file
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

  @Test
  fun `it should apply an insertion and deletion patch on a non-empty text`() {
    // given
    val originalText = """
      Apple
      Orange
      Grapes
      Mango
    """.trimIndent()

    val patch = """
      @@ -1,4 +1,4 @@
      +Avocado
       Apple
      -Orange
       Grapes
       Mango
    """.trimIndent()

    // when
    val patchedText = applyPatch(originalText, patch)

    // then
    assertThat(patchedText)
      .isEqualTo(
        """
          Avocado
          Apple
          Grapes
          Mango
        """.trimIndent()
      )
  }

  @Test
  fun `it should apply multiple patches on a single text`() {
    // given
    val originalText = """
      Apple
      Orange
      Grapes
      Mango
      Cucumber
      Onion
      Carrots
      Shallot
      Strawberry
      Banana
      Leek
      Cheese
      Shrimp
    """.trimIndent()

    val patch = """
      @@ -1,5 +1,5 @@
      +Avocado
       Apple
      -Orange
       Grapes
       Mango
       Cucumber
      @@ -9,5 +9,4 @@
       Strawberry
       Banana
       Leek
      -Cheese
      -Shrimp
      \ No newline at end of file
      +Cheese
      \ No newline at end of file
    """.trimIndent()

    // when
    val patchedText = applyPatch(originalText, patch)

    // then
    assertThat(patchedText)
      .isEqualTo(
        """
          Avocado
          Apple
          Grapes
          Mango
          Cucumber
          Onion
          Carrots
          Shallot
          Strawberry
          Banana
          Leek
          Cheese
        """.trimIndent()
      )
  }

  @Test
  fun `it should apply git-style unified patches with file information`() {
    // given
    val text = "Hello, world!"

    val patch = """
      --- a.txt	2021-04-21 12:19:34.000000000 +0530
      +++ b.txt	2021-04-21 12:19:47.000000000 +0530
      @@ -1 +1 @@
      -Hello, world!
      \ No newline at end of file
      +Hello, stranger!
      \ No newline at end of file
    """.trimIndent()

    // when
    val patchedText = applyPatch(text, patch)

    // then
    assertThat(patchedText)
      .isEqualTo("Hello, stranger!")
  }
}
