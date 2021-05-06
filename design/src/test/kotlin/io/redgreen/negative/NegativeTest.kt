package io.redgreen.negative

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opentest4j.AssertionFailedError

@Suppress("SameParameterValue")
class NegativeTest {
  @Test
  fun `it should show method name of the caller`() {
    val assertionError = assertThrows<AssertionFailedError> {
      failOnInvocation(1)
    }

    assertThat(assertionError.message)
      .contains("it should show method name of the caller()")
  }

  @Test
  fun `it should show empty parameter list for caller with no parameters`() {
    val assertionError = assertThrows<AssertionFailedError> {
      failOnInvocation(1)
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

  @Test
  fun `it should detect functions with primitive parameter types`() {
    val assertionError = assertThrows<AssertionFailedError> {
      add(3, 5)
    }

    assertThat(assertionError.message)
      .contains("add(int, int)")
  }

  private fun greet(name: String) {
    println("Hello, $name!")
    failOnInvocation(1, name)
  }

  private fun greet(firstName: String, lastName: String) {
    println("Hello, $firstName $lastName!")
    failOnInvocation(1, firstName, lastName)
  }

  private fun add(x: Int, y: Int): Int {
    failOnInvocation(1, x, y)
    return x + y
  }
}
