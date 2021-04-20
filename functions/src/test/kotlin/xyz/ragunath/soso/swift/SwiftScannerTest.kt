package xyz.ragunath.soso.swift

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import xyz.ragunath.soso.PossibleFunction

class SwiftScannerTest {
  @Test
  fun `it returns an empty list when there are no swift functions`() {
    val noSwiftHere = """
      There is no swift code here!
    """.trimIndent()

    assertThat(swiftScan(noSwiftHere))
      .isEmpty()
  }

  @Test
  fun `it can detect a snippet that has one function`() {
    val oneFunction = """
      func oneFunction() {
        print("The one and only function!")
      }
    """.trimIndent()

    assertThat(swiftScan(oneFunction))
      .containsExactly(PossibleFunction("oneFunction", 1))
      .inOrder()
  }
}
