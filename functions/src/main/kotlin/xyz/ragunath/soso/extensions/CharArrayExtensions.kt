package xyz.ragunath.soso.extensions

fun CharArray.push(char: Char) {
  for (i in 0..this.size - 2) {
    this[i] = this[i + 1]
  }
  this[this.size - 1] = char
}
