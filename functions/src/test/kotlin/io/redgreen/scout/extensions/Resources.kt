package io.redgreen.scout.extensions

import io.redgreen.scout.DetectFunctionsTest

fun readResourceFile(filePath: String): String {
  return DetectFunctionsTest::class.java
    .getResourceAsStream(filePath)
    .reader()
    .readText()
}
