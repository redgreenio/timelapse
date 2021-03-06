package io.redgreen.architecture.mobius.dev

import com.spotify.mobius.First
import com.spotify.mobius.MobiusLoop
import com.spotify.mobius.Next

class DevLogger<M, E, F> : MobiusLoop.Logger<M, E,  F> {
  private companion object {
    private const val printLogs = false
  }

  override fun beforeInit(model: M) {
    debug { "beforeInit: $model" }
  }

  override fun afterInit(model: M, result: First<M, F>) {
    debug { "afterInit: $model, result: $result" }
  }

  override fun exceptionDuringInit(model: M, exception: Throwable) {
    // no-op
  }

  override fun beforeUpdate(model: M, event: E) {
    // no-op
  }

  override fun afterUpdate(model: M, event: E, result: Next<M, F>) {
    debug { "afterUpdate: $model, event: $event, result: $result" }
  }

  override fun exceptionDuringUpdate(model: M, event: E, exception: Throwable) {
    // no-op
  }

  private fun debug(lazyMessage: () -> String) {
    if (printLogs) {
      println(lazyMessage())
    }
  }
}
