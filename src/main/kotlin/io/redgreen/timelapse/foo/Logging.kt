package io.redgreen.timelapse.foo

internal fun debug(message: () -> String) {
  println(message())
}
