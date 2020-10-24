package io.redgreen.timelapse.mobius

/**
 * The [AsyncOp] type represents asynchronous tasks at hand. This are commonly used for but not limited to
 * disk operations, network operations, database operations, etc.,
 *
 * @param C the content type when the operation completes successfully.
 * @param R the reason in case of a failure.
 */
sealed class AsyncOp<in C, in R> {
  @Suppress("UNCHECKED_CAST")
  companion object {
    fun <C, R> idle(): AsyncOp<C, R> =
      Idle as AsyncOp<C, R>

    fun <C, R> inFlight(): AsyncOp<C, R> =
      InFlight as AsyncOp<C, R>

    fun <C, R> content(content: C): AsyncOp<C, R> =
      Content(content) as AsyncOp<C, R>

    fun <C, R> failure(reason: R): AsyncOp<C, R> =
      Failure(reason) as AsyncOp<C, R>
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
  data class Failure<R>(val reason: R) : AsyncOp<Nothing, R>()

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
