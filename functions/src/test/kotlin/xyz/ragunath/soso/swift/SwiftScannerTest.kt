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

    assertThat(swiftScan(addSubtractFunctions))
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

    assertThat(swiftScan(functionWithoutBody))
      .isEmpty()
  }

  @Test
  fun `it can skip lines that have commented out func keywords`() {
    val comments = """
      // this has a func keyword
      // func printIt(message: String)
      // func printItAgain(message: String) {}
    """.trimIndent()

    assertThat(swiftScan(comments))
      .isEmpty()
  }

  @Test
  fun `it can detect single line functions`() {
    val singleLineFunction = """
      func singleLineFunction() {}
    """.trimIndent()

    assertThat(swiftScan(singleLineFunction))
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

    assertThat(swiftScan(functionDefinitionAcrossMultipleLines))
      .containsExactly(PossibleFunction("add", 1))
      .inOrder()
  }

  @Test
  fun `it can ignore function declarations`() {
    val noFunctionDefinitions = """
      protocol CombineLatestProtocol : class {
          func next(_ index: Int)
      }

      class CombineLatestSink<O: ObserverType>
          : Sink<O>
          , CombineLatestProtocol {
          typealias Element = O.E
         
          init(arity: Int, observer: O, cancel: Cancelable) {
              _arity = arity
              _hasValue = [Bool](repeating: false, count: arity)
              _isDone = [Bool](repeating: false, count: arity)
              
              super.init(observer: observer, cancel: cancel)
          }
      }
    """.trimIndent()

    assertThat(swiftScan(noFunctionDefinitions))
      .isEmpty()
  }
}