import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

class CanaryTest {
  @Test
  fun `junit and truth are setup`() {
    assertThat(true)
      .isTrue()
  }

  @Test
  fun `mockito is setup`() {
    assertThat(mock<X>())
      .isNotNull()
  }

  interface X
}
