package io.redgreen.timelapse.geometry

fun calculateSlope(x1: Double, y1: Int, x2: Double, y2: Int): Double =
  (y2 - y1) / (x2 - x1)
