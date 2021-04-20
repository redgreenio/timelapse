package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import xyz.ragunath.soso.extensions.push

private const val NULL_CHAR = '\u0000'

class CharArrayExtensionTest {
  @Test
  fun `it can push elements into a character array`() {
    val charArray = CharArray(4)
    charArray.push('1')
    assertThat(String(charArray))
      .isEqualTo("$NULL_CHAR$NULL_CHAR${NULL_CHAR}1")
  }
}
