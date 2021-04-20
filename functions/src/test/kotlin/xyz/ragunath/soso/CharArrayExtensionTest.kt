package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xyz.ragunath.soso.extensions.push

private const val NULL_CHAR = '\u0000'

class CharArrayExtensionTest {
  private val charArray = CharArray(4)

  @Test
  fun `it can push elements into an array`() {
    charArray.push('1')
    assertThat(String(charArray))
      .isEqualTo("$NULL_CHAR$NULL_CHAR${NULL_CHAR}1")
  }

  @Test
  fun `it can push n elements into an array`() {
    with(charArray) {
      push('1')
      push('2')
      push('3')
      push('4')
    }

    assertThat(String(charArray))
      .isEqualTo("1234")
  }

  @Test
  fun `it can overwrite the old buffer when new chars are pushed into it`() {
    with(charArray) {
      push('1'); push('2'); push('3'); push('4')
      push('5'); push('6')
    }

    assertThat(String(charArray))
      .isEqualTo("3456")
  }

  @Test
  fun `it throws an exception when chars are pushed into an array with length 0`() {
    val exception = assertThrows<IllegalStateException> {
      val zeroLengthArray = CharArray(0)
      zeroLengthArray.push('a')
    }
    assertThat(exception.message)
      .isEqualTo("Cannot push into a zero-length array")
  }
}
