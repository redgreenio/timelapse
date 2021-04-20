package xyz.ragunath.soso.extensions

fun CharArray.push(char: Char) {
  check(this.isNotEmpty()) { "Cannot push into a zero-length array" }

  for (i in 0..this.size - 2) {
    this[i] = this[i + 1]
  }
  this[this.size - 1] = char
}
