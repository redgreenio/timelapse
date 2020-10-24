package io.redgreen.timelapse.mobius

import com.spotify.mobius.EventSource
import com.spotify.mobius.disposables.Disposable
import com.spotify.mobius.functions.Consumer
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * This class is from the Mobius Android sample - https://github.com/spotify/mobius-android-sample
 **/
class DeferredEventSource<E> : EventSource<E> {
  private val events: BlockingQueue<E> = LinkedBlockingQueue()

  override fun subscribe(eventConsumer: Consumer<E>): Disposable {
    val run = AtomicBoolean(true)

    val thread = Thread {
      while (run.get()) {
        try {
          val event = events.take()

          if (run.get()) {
            eventConsumer.accept(event)
          }
        } catch (e: InterruptedException) {
          // Nothing to do here
        }
      }
    }

    thread.start()

    return Disposable {
      run.set(false)
      thread.interrupt()
    }
  }

  @Synchronized
  fun notify(event: E) {
    events.offer(event)
  }
}
