package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ParseResultTest {
  @Test
  fun `it returns a length of 1 for single line functions`() {
    val result = ParseResult.wellFormedFunction(1, 1, 5)
    assertThat(result.length)
      .isEqualTo(1)
  }

  @Test
  fun `it returns the actual length for a function that spans across multiple lines`() {
    val result = ParseResult.wellFormedFunction(11, 17, 2)
    assertThat(result.length)
      .isEqualTo(7)
  }

  @Test
  fun `it throws an exception when start line is greater than the end line`() {
    val exception = assertThrows<IllegalStateException> {
      ParseResult.wellFormedFunction(11, 8, 1)
    }
    assertThat(exception.message)
      .isEqualTo("`startLine`: 11 cannot be greater than `endLine`: 8")
  }

  @Test
  fun `it throws an exception when depth is not equal to zero for invalid start and end lines`() {
    val exception = assertThrows<IllegalStateException> {
      ParseResult.wellFormedFunction(0, 0, 1)
    }
    assertThat(exception.message)
      .isEqualTo("`depth` must be zero for a non-existent function, but was `1`")
  }

  @Test
  fun `it throws an exception when start line is negative`() {
    val exception = assertThrows<IllegalStateException> {
      ParseResult.wellFormedFunction(-1, 0, 1)
    }
    assertThat(exception.message)
      .isEqualTo("`startLine`: -1 should be a positive integer")
  }

  @Test
  fun `it throws an exception when end line is negative`() {
    val exception = assertThrows<IllegalStateException> {
      ParseResult.wellFormedFunction(5, -4, 1)
    }
    assertThat(exception.message)
      .isEqualTo("`endLine`: -4 should be a positive integer")
  }

  @Test
  fun `it throws an exception when depth is negative`() {
    val exception = assertThrows<IllegalStateException> {
      ParseResult.wellFormedFunction(1, 5, -1)
    }
    assertThat(exception.message)
      .isEqualTo("`depth`: -1 should be a positive integer")
  }
}
