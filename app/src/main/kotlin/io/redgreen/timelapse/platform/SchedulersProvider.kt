package io.redgreen.timelapse.platform

import io.reactivex.rxjava3.core.Scheduler

interface SchedulersProvider {
  fun ui(): Scheduler
}
