package io.redgreen.timelapse.geometry

fun calculateSlope(x1: Int, y1: Int, x2: Int, y2: Int): Double =
  (y2 - y1).toDouble() / (x2 - x1)
