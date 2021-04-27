package io.redgreen.scout.extensions

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional
import java.lang.Character.MIN_VALUE as NULL_CHAR

class CharArrayExtensionsTest {
  @Nested
  inner class Push {
    private val size4CharArray = CharArray(4)

    @Test
    fun `it can push an element into an array`() {
      val nullChar = '\u0000'
      with(size4CharArray) {
        // when
        push('1')

        // then
        assertContainsExactly(nullChar, nullChar, nullChar, '1')
      }
    }

    @Test
    fun `it can push 'n' elements into an array of size 'n'`() {
      with(size4CharArray) {
        // when
        push('1')
        push('2')
        push('3')
        push('4')

        // then
        assertContainsExactly('1', '2', '3', '4')
      }
    }

    @Test
    fun `it can overwrite old content when newer chars are pushed into the array`() {
      with(size4CharArray) {
        // when
        push('1'); push('2'); push('3')
        push('4'); push('5'); push('6')

        // then
        assertContainsExactly('3', '4', '5', '6')
      }
    }
  }

  @Nested
  inner class Size {
    @Test
    fun `it throws an exception when chars are pushed into an empty array`() {
      val exception = assertThrows<IllegalStateException> {
        // when
        CharArray(0).also { it.push('a') }
      }

      // then
      assertThat(exception.message)
        .isEqualTo("Cannot push into an empty array")
    }

    @Test
    fun `it can push items into an array of size 1`() {
      with(CharArray(1)) {
        // when
        push('a')

        // then
        assertContainsExactly('a')
      }
    }

    @Test
    fun `it can overwrite items in an array of size 1`() {
      with(CharArray(1)) {
        // when
        push('a')
        push('b')

        // then
        assertContainsExactly('b')
      }
    }

    @Test
    fun `it can push items into an array of size 2`() {
      with(CharArray(2)) {
        // when
        push('a')
        push('c')

        // then
        assertContainsExactly('a', 'c')
      }
    }

    @Test
    fun `it can overwrite items in an array of size 2`() {
      with(CharArray(2)) {
        // when
        push('a')
        push('b')
        push('c')
        push('d')

        // then
        assertContainsExactly('c', 'd')
      }
    }
  }

  @Nested
  inner class EndsWith {
    private val alphabetsCharArray = CharArray(4)

    @BeforeEach
    fun setup() {
      with(alphabetsCharArray) {
        push('a')
        push('b')
        push('c')
        push('d')
      }
    }

    @Test
    fun `it returns true if both array contents are equal`() {
      val abcd = "abcd".toCharArray()
      assertThat(alphabetsCharArray.endsWith(abcd))
        .isTrue()
    }

    @Test
    fun `it returns true if the array ends with a subset of chars`() {
      val bcd = "bcd".toCharArray()
      assertThat(alphabetsCharArray.endsWith(bcd))
        .isTrue()
    }

    @Test
    fun `it returns false if the contents don't match`() {
      val numbers1234 = "1234".toCharArray()
      assertThat(alphabetsCharArray.endsWith(numbers1234))
        .isFalse()
    }

    @Test
    fun `it returns false if the passed in array is larger than the given array`() {
      val abcde = "abcde".toCharArray()
      assertThat(alphabetsCharArray.endsWith(abcde))
        .isFalse()
    }

    @Test
    fun `it returns false if the passed in array is empty`() {
      val emptyArray = CharArray(0)
      assertThat(alphabetsCharArray.endsWith(emptyArray))
        .isFalse()
    }
  }

  @Nested
  inner class Top {
    @Test
    fun `it returns empty if the char buffer is empty`() {
      assertThat(CharArray(0).top())
        .isEqualTo(Optional.empty<Char>())
    }

    @Test
    fun `it returns empty if the char buffer is filled with null sentinel chars`() {
      assertThat(CharArray(4) { NULL_CHAR }.top())
        .isEqualTo(Optional.empty<Char>())
    }

    @Test
    fun `it returns the latest char for a partially filled buffer`() {
      // given
      val charArray = CharArray(4) { NULL_CHAR }

      // when
      with(charArray) {
        push('1')
        push('2')
      }

      // then
      assertThat(charArray.top().get())
        .isEqualTo('2')
    }
  }

  private fun CharArray.assertContainsExactly(vararg chars: Char) {
    assertThat(this.toList())
      .containsExactly(*chars.toTypedArray())
      .inOrder()
  }
}
