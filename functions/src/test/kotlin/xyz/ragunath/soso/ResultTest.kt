package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResultTest {
  @Test
  fun `it returns a length of 0 when the result is empty`() {
    val result = Result.EMPTY
    assertThat(result.length)
      .isEqualTo(0)
  }

  @Test
  fun `it returns a length of 1 for single line functions`() {
    val result = Result.with(1, 1, 5)
    assertThat(result.length)
      .isEqualTo(1)
  }

  @Test
  fun `it returns the actual length for a function that spans across multiple lines`() {
    val result = Result.with(11, 17, 2)
    assertThat(result.length)
      .isEqualTo(7)
  }

  // TODO(rj) 12/Oct/19 - Assert error messages as well.
  @Test(expected = IllegalStateException::class)
  fun `it throws an exception when start line is greater than the end line`() {
    Result.with(11, 8, 1)
  }

  @Test(expected = IllegalStateException::class)
  fun `it throws an exception when depth is not equal to zero for invalid start and end lines`() {
    Result.with(0, 0, 1)
  }

  @Test(expected = IllegalStateException::class)
  fun `it throws an exception when start line is negative`() {
    Result.with(-1, 0, 1)
  }

  @Test(expected = IllegalStateException::class)
  fun `it throws an exception when end line is negative`() {
    Result.with(5, -4, 1)
  }

  @Test(expected = IllegalStateException::class)
  fun `it throws an exception when depth is negative`() {
    Result.with(1, 5, -1)
  }
}
