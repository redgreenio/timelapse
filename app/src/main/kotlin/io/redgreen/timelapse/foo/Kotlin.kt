package io.redgreen.timelapse.foo

import kotlin.LazyThreadSafetyMode.NONE

fun <T> fastLazy(initializer: () -> T): Lazy<T> =
  lazy(NONE, initializer)
