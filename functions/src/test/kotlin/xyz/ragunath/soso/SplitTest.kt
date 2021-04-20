package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SplitTest {
  @Test
  fun `it returns the original string when the string is empty`() {
    val emptyText = ""
    assertThat(split(emptyText, 1))
      .isEqualTo(emptyText)
  }

  @Test
  fun `it returns the original string when the text is blank`() {
    val blankOneLineText = "  "
    assertThat(split(blankOneLineText, 1))
      .isEqualTo(blankOneLineText)
  }

  @Test
  fun `it returns the original string when there is just one line of text`() {
    val oneLineText = "This is just a line of text"
    assertThat(split(oneLineText, 1))
      .isEqualTo(oneLineText)
  }

  @Test(expected = IllegalArgumentException::class)
  fun `it throws an exception when the split line number is greater than the actual number of lines`() {
    val oneLineText = "One line text again"
    split(oneLineText, 2)
  }
}
