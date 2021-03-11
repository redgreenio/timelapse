import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

class CanaryTest {
  @Test
  fun `junit test environment is setup`() {
    assertThat(true)
      .isTrue()
  }

  @Test
  fun `mockito can create mocks`() {
    assertThat(mock<X>())
      .isNotNull()
  }

  interface X
}
