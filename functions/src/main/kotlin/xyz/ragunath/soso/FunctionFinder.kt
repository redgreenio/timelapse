package xyz.ragunath.soso

private val buffer = CharArray(4)

fun findFunction(text: String): Location {
  var row = 1
  var startRow = -1
  val chars = text.toCharArray()

  for (char in chars) {
    buffer.push(char)
    if (char == '\n') row++

    if (buffer.has("fun")) {
      startRow = row
    }
  }

  return Location(startRow, row)
}

private fun CharArray.push(char: Char) {
  val bufferSize = this.size
    .also { check(it != 0) { "Can't push into an empty `CharArray`" } }

  for (i in 1 until bufferSize) {
    this[i - 1] = this[i]
  }
  this[bufferSize - 1] = char
}

private fun CharArray.has(text: String): Boolean =
  joinToString("").contains(text)

data class Location(
  val start: Int,
  val end: Int
)
