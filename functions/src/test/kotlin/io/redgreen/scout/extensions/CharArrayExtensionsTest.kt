package io.redgreen.scout.extensions

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CharArrayExtensionsTest {
  private val nullChar = '\u0000'
  private val charArray = CharArray(4)

  @Test
  fun `it can push elements into an array`() {
    charArray.push('1')
    assertThat(String(charArray))
      .isEqualTo("$nullChar$nullChar${nullChar}1")
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

  @Test
  fun `it can push items into a char array of size 1`() {
    val size1CharArray = CharArray(1)
    size1CharArray.push('a')

    assertThat(String(size1CharArray))
      .isEqualTo("a")
  }

  @Test
  fun `it can overwrite items in a char array of size 1`() {
    val size1CharArray = CharArray(1)
    with(size1CharArray) {
      push('a')
      push('b')
    }

    assertThat(String(size1CharArray))
      .isEqualTo("b")
  }

  @Test
  fun `it can push items into a char array of size 2`() {
    val size2CharArray = CharArray(2)
    with(size2CharArray) {
      push('a')
      push('c')
    }

    assertThat(String(size2CharArray))
      .isEqualTo("ac")
  }

  @Test
  fun `it can overwrite items in a char array of size 2`() {
    val size2CharArray = CharArray(2)
    with(size2CharArray) {
      push('a')
      push('b')
      push('c')
      push('d')
    }

    assertThat(String(size2CharArray))
      .isEqualTo("cd")
  }
}
