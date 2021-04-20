package xyz.ragunath.soso

interface FunctionScanner {
  fun scan(snippet: String): List<PossibleFunction>
}
