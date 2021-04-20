package io.redgreen.architecture.mobius

/**
 * The [AsyncOp] type helps represent state of an asynchronous operation. It is commonly used for, but not limited
 * to represent disk operations, network operations, database operations, etc.
 *
 * @param C the content type if the operation is successful.
 * @param F reason in case of a failure.
 */
sealed class AsyncOp<in C, in F> {
  @Suppress("UNCHECKED_CAST")
  companion object {
    fun <C, F> idle(): AsyncOp<C, F> =
      Idle as AsyncOp<C, F>

    fun <C, F> inFlight(): AsyncOp<C, F> =
      InFlight as AsyncOp<C, F>

    fun <C, F> content(content: C): AsyncOp<C, F> =
      Content(content) as AsyncOp<C, F>

    fun <C, F> failure(reason: F): AsyncOp<C, F> =
      Failure(reason) as AsyncOp<C, F>
  }

  /**
   * Represents a situation where the async operation has not been started.
   */
  object Idle : AsyncOp<Nothing, Nothing>()

  /**
   * Represents an async operation in progress, this is where the UI may show a
   * non-deterministic loader on the screen.
   */
  object InFlight : AsyncOp<Nothing, Nothing>()

  /**
   * The async operation completed successfully and the requested information is
   * available for use. The content may be modeled using a sealed class if necessary.
   */
  data class Content<C>(val content: C) : AsyncOp<C, Nothing>()

  /**
   * The async operation failed to complete and it contains the reason why. The reason
   * may be modeled using a sealed class if necessary.
   */
  data class Failure<F>(val reason: F) : AsyncOp<Nothing, F>()

  /**
   * Convenience property to use with `when` blocks to surpass verbose type casting.
   */
  val value: AsyncOp<Nothing, Nothing>
    get() {
      return when (this) {
        Idle -> Idle
        InFlight -> InFlight
        is Content -> this
        is Failure -> this
      }
    }
}
