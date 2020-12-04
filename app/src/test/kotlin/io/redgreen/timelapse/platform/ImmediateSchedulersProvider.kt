package io.redgreen.timelapse.platform

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

object ImmediateSchedulersProvider : SchedulersProvider {
  override fun ui(): Scheduler = Schedulers.trampoline()
}
