package io.redgreen.timelapse.platform

import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler

object JavaFxSchedulersProvider : SchedulersProvider {
  override fun ui(): Scheduler = RxJavaBridge.toV3Scheduler(JavaFxScheduler.platform())
  override fun io(): Scheduler = Schedulers.io()
}
