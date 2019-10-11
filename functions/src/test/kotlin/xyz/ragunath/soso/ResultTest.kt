package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResultTest {
  @Test
  fun `when no function as found, then return a length of zero`() {
    val result = Result.with(0, 0, 0)
    assertThat(result.length)
      .isEqualTo(0)
  }

  @Test
  fun `when a single line function was found, then return a length of 1`() {
    val result = Result.with(1, 1, 5)
    assertThat(result.length)
      .isEqualTo(1)
  }

  @Test
  fun `when a function spans across multiple lines, then return its actual length`() {
    val result = Result.with(11, 17, 2)
    assertThat(result.length)
      .isEqualTo(7)
  }

  @Test(expected = IllegalStateException::class)
  fun `when start line is greater than the end line, then throw an exception`() {
    Result.with(11, 8, 1)
  }

  @Test(expected = IllegalStateException::class)
  fun `when a function is non-existent, then the depth must be zero`() {
    Result.with(0, 0, 1)
  }

  @Test(expected = IllegalStateException::class)
  fun `when start line is not positive, throw an exception`() {
    Result.with(-1, 0, 1)
  }

  @Test(expected = IllegalStateException::class)
  fun `when end line is not positive, throw an exception`() {
    Result.with(5, -4, 1)
  }

  @Test(expected = java.lang.IllegalStateException::class)
  fun `when depth is not positive, throw an exception`() {
    Result.with(1, 5, -1)
  }
}
