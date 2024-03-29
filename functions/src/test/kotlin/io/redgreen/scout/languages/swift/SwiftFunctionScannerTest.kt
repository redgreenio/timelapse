package io.redgreen.scout.languages.swift

import com.google.common.truth.Truth.assertThat
import io.redgreen.scout.PossibleFunction
import org.junit.jupiter.api.Test

class SwiftFunctionScannerTest {
  @Test
  fun `it returns an empty list when there are no swift functions`() {
    val noSwiftHere = """
      There is no swift code here!
    """.trimIndent()

    assertThat(SwiftFunctionScanner.scan(noSwiftHere))
      .isEmpty()
  }

  @Test
  fun `it can detect a snippet that has one function`() {
    val oneFunction = """
      func oneFunction() {
        print("The one and only function!")
      }
    """.trimIndent()

    assertThat(SwiftFunctionScanner.scan(oneFunction))
      .containsExactly(PossibleFunction("oneFunction", 1))
      .inOrder()
  }

  @Test
  fun `it can detect more than one function from a snippet`() {
    val addSubtractFunctions = """
      func add(a: Int, b: Int) -> Int {
        return a + b
      }

      func subtract(a: Int, b: Int) -> Int {
        return a - b
      }
    """.trimIndent()

    assertThat(SwiftFunctionScanner.scan(addSubtractFunctions))
      .containsExactly(
        PossibleFunction("add", 1),
        PossibleFunction("subtract", 5)
      )
      .inOrder()
  }

  @Test
  fun `it can skip functions that do not have a body`() {
    val functionWithoutBody = """
      protocol RandomNumberGenerator {
        func random() -> Double
      }
    """.trimIndent()

    assertThat(SwiftFunctionScanner.scan(functionWithoutBody))
      .isEmpty()
  }

  @Test
  fun `it can skip lines that have commented out func keywords`() {
    val comments = """
      // this has a func keyword
      // func printIt(message: String)
      // func printItAgain(message: String) {}
    """.trimIndent()

    assertThat(SwiftFunctionScanner.scan(comments))
      .isEmpty()
  }

  @Test
  fun `it can detect single line functions`() {
    val singleLineFunction = """
      func singleLineFunction() {}
    """.trimIndent()

    assertThat(SwiftFunctionScanner.scan(singleLineFunction))
      .containsExactly(PossibleFunction("singleLineFunction", 1))
      .inOrder()
  }

  @Test
  fun `it can detect function definition that spans across multiple lines`() {
    val functionDefinitionAcrossMultipleLines = """
      private func add(_ a: Int,
                       _ b: Int) {
        return a + b
      }
    """.trimIndent()

    assertThat(SwiftFunctionScanner.scan(functionDefinitionAcrossMultipleLines))
      .containsExactly(PossibleFunction("add", 1))
      .inOrder()
  }

  @Test
  fun `it can ignore function declarations`() {
    val noFunctionDefinitions = """
      protocol CombineLatestProtocol : class {
          func next(_ index: Int)
          func previous(_ index: Int)
      }

      class CombineLatestSink<O: ObserverType>
          : Sink<O>
          , CombineLatestProtocol {
          typealias Element = O.E
      }
    """.trimIndent()

    assertThat(SwiftFunctionScanner.scan(noFunctionDefinitions))
      .isEmpty()
  }

  @Test
  fun `it can detect init functions`() {
    val initFunction = """
      init(buffers: MediaPlaybackBuffers, extraDecodedVideoFrames: [MediaTrackFrame], timestamp: CMTime) {
        self.buffers = buffers
        self.extraDecodedVideoFrames = extraDecodedVideoFrames
        self.timestamp = timestamp
      }
    """.trimIndent()

    assertThat(SwiftFunctionScanner.scan(initFunction))
      .containsExactly(PossibleFunction("init", 1))
      .inOrder()
  }

  @Test
  fun `it can detect init functions with closures`() {
    val initFunctionWithClosure = """
      class InstantPageItemArguments {
        let theme: InstantPageTheme
        let openMedia:(InstantPageMedia)->Void

        init(theme: InstantPageTheme, openMedia: @escaping (InstantPageMedia) -> Void) {
          self.theme = theme
          self.openMedia = openMedia
        }
      }
    """.trimIndent()

    assertThat(SwiftFunctionScanner.scan(initFunctionWithClosure))
      .containsExactly(PossibleFunction("init", 5))
      .inOrder()
  }
}
