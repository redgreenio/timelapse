package xyz.ragunath.soso

typealias Depth = Int

object SoSo {
  fun maximumDepthOf(code: String): Depth {
    return code
      .fold(0) { depth, char ->
        if (char == '{') depth + 1 else depth
      }
  }
}
