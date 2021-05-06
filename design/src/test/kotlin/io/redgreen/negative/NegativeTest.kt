package io.redgreen.negative

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opentest4j.AssertionFailedError

class NegativeTest {
  @Test
  fun `it should show method name of the caller`() {
    val assertionError = assertThrows<AssertionFailedError> {
      failOnUnexpectedCallback(1)
    }

    assertThat(assertionError.message)
      .contains("it should show method name of the caller()")
  }

  @Test
  fun `it should show empty parameter list for caller with no parameters`() {
    val assertionError = assertThrows<AssertionFailedError> {
      failOnUnexpectedCallback(1)
    }

    assertThat(assertionError.message)
      .contains("it should show empty parameter list for caller with no parameters()")
  }

  @Test
  fun `it should show function parameters for a caller with parameters`() {
    val assertionError = assertThrows<AssertionFailedError> {
      greet("world")
    }

    assertThat(assertionError.message)
      .contains("greet(String)")
  }

  @Test
  fun `it should show function parameters for a caller with overloaded parameters`() {
    val assertionError = assertThrows<AssertionFailedError> {
      greet("Jane", "Doe")
    }

    assertThat(assertionError.message)
      .contains("greet(String, String)")
  }

  @Suppress("SameParameterValue")
  private fun greet(name: String) {
    println("Hello, $name!")
    failOnUnexpectedCallback(1, name)
  }

  @Suppress("SameParameterValue")
  private fun greet(firstName: String, lastName: String) {
    println("Hello, $firstName $lastName!")
    failOnUnexpectedCallback(1, firstName, lastName)
  }
}
