package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.Test

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

  @Test(expected = IllegalArgumentException::class)
  fun `it throws an exception when the split line number is greater than the actual number of lines`() {
    val oneLineText = "One line text again"
    split(oneLineText, 2)
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
        "Line one",

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
          Line one
          Line two
        """.trimIndent(),

        """
          Line three
          Line four
        """.trimIndent()
      )
      .inOrder()
  }
}
