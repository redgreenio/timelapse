package io.redgreen.timelapse.openrepo.view

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.openrepo.view.WelcomeMessage.Greeter
import org.junit.jupiter.api.Test

class WelcomeMessageTest {
  @Test
  fun `it should display the full name if there's only first name`() {
    assertThat(Greeter("Joe").displayName)
      .isEqualTo("Joe")
  }

  @Test
  fun `it should display the first name if the name has first and last names`() {
    assertThat(Greeter("Jane Doe").displayName)
      .isEqualTo("Jane")
  }

  @Test
  fun `it should display the first name if the name has first, last and middle names`() {
    assertThat(Greeter("Miles Gonzalo Morales").displayName)
      .isEqualTo("Miles")
  }

  @Test
  fun `it should trim leading spaces from the first name`() {
    assertThat(Greeter("   Ajay Kumar").displayName)
      .isEqualTo("Ajay")
  }
}
