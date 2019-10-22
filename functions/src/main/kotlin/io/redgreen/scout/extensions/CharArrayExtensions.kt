package io.redgreen.scout.extensions

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
