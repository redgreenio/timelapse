package io.redgreen.scout.extensions

import java.util.Optional
import java.lang.Character.MIN_VALUE as NULL_CHAR

fun CharArray.push(char: Char) {
  check(this.isNotEmpty()) { "Cannot push into an empty array" }

  for (i in 0..this.size - 2) {
    this[i] = this[i + 1]
  }
  this[this.size - 1] = char
}

fun CharArray.endsWith(chars: CharArray): Boolean {
  if (this.size < chars.size || chars.isEmpty()) {
    return false
  }

  var matches = true
  val thisStartIndex = this.lastIndex - chars.lastIndex
  for (thisIndex in thisStartIndex until this.size) {
    val charsIndex = thisIndex + (chars.size - this.size)
    matches = this[thisIndex] == chars[charsIndex]
    if (!matches) break
  }
  return matches
}

fun CharArray.top(): Optional<Char> {
  if (this.isNotEmpty() && this[lastIndex] != NULL_CHAR) {
    return Optional.of(this[lastIndex])
  }
  return Optional.empty()
}

fun CharArray.contents(): Optional<String> {
  if (this.isNotEmpty() && this[lastIndex] != NULL_CHAR) {
    return Optional.of(this.filter { it != NULL_CHAR }.joinToString(""))
  }
  return Optional.empty()
}
