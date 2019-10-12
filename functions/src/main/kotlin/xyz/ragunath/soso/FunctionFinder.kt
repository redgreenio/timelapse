package xyz.ragunath.soso

fun findPossibleFunctions(snippet: String): List<PossibleFunction> {
  if (snippet.isBlank()) return emptyList()

  val possibleFunctions = mutableListOf<PossibleFunction>()
  var lineNumber = 1
  val lines = snippet.split('\n')
  for (line in lines) {
    if (line.contains("fun")) {
      possibleFunctions.add(PossibleFunction(lineNumber))
    }
    lineNumber++
  }

  return possibleFunctions.toList()
}
