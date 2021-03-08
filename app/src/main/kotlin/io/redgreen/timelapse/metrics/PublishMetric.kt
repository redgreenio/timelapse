package io.redgreen.timelapse.metrics

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleSource
import io.reactivex.rxjava3.core.SingleTransformer
import io.reactivex.rxjava3.schedulers.Timed
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import org.greenrobot.eventbus.EventBus

fun <T, M> Single<T>.publishMetric(
  toMetric: (Duration) -> M
): Single<T> =
  this.compose(PublishMetric(toMetric))

class PublishMetric<T, M>(
  private val mapToMetric: (Duration) -> M
) : SingleTransformer<T, T> {
  override fun apply(upstream: Single<T>): SingleSource<T> {
    return upstream
      .timeInterval()
      .doOnSuccess { EventBus.getDefault().post(mapToMetric(it.toDuration())) }
      .map(Timed<T>::value)
  }

  private fun Timed<T>.toDuration(): Duration =
    Duration.of(this.time(TimeUnit.MILLISECONDS), ChronoUnit.MILLIS)
}
