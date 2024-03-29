package io.redgreen.scout

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SplitTest {
  @Test
  fun `it returns the original string when the string is empty`() {
    val emptyText = ""
    assertThat(split(emptyText, 1))
      .containsExactly(emptyText)
      .inOrder()
  }

  @Test
  fun `it returns the original string when the text is blank`() {
    val blankOneLineText = "  "
    assertThat(split(blankOneLineText, 1))
      .containsExactly(blankOneLineText)
      .inOrder()
  }

  @Test
  fun `it returns the original string when there is just one line of text`() {
    val oneLineText = "This is just a line of text"
    assertThat(split(oneLineText, 1))
      .containsExactly(oneLineText)
      .inOrder()
  }

  @Test
  fun `it throws an exception when the split line number is greater than the number of lines`() {
    val oneLineText = "One line text again"
    val exception = assertThrows<IllegalArgumentException> {
      split(oneLineText, 2)
    }
    assertThat(exception.message)
      .isEqualTo("`splitLineNumber`: 2 cannot be greater than the number of lines: 1")
  }

  @Test
  fun `it can split the first line and the rest based on the provided split line position`() {
    val threeLines = """
      Line one
      Line two
      Line three
    """.trimIndent()

    assertThat(split(threeLines, 2))
      .containsExactly(
        """
        Line two
        Line three
        """.trimIndent()
      )
      .inOrder()
  }

  @Test
  fun `it can split a block of text based on any line number`() {
    val fourLines = """
      Line one
      Line two
      Line three
      Line four
    """.trimIndent()

    assertThat(split(fourLines, 3))
      .containsExactly(
        """
          Line three
          Line four
        """.trimIndent()
      )
      .inOrder()
  }

  @Test
  fun `it returns entire text when split line is 1`() {
    val threeLines = """
      Line one
      Line two
      Line three
    """.trimIndent()

    assertThat(split(threeLines, 1))
      .containsExactly(threeLines)
      .inOrder()
  }

  @Test
  fun `it can split multiple lines based on multiple line numbers`() {
    val fiveLines = """
      Line one
      Line two
      Line three
      Line four
      Line five
    """.trimIndent()

    assertThat(split(fiveLines, 3, 4))
      .containsExactly(
        "Line three",
        """
          Line four
          Line five
        """.trimIndent()
      )
  }
}
