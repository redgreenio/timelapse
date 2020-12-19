package io.redgreen.timelapse.search

import io.redgreen.timelapse.search.Occurrence.None
import io.redgreen.timelapse.search.Occurrence.Segment

class FilePathIndex(
  private val filePaths: List<String>
) {
  companion object {
    fun from(filePaths: List<String>): FilePathIndex =
      FilePathIndex(filePaths)
  }

  fun search(term: String): List<Match> {
    return filePaths
      .map { filePath -> filePath to getOccurrences(filePath, term) }
      .filter { (_, occurrences) -> occurrences.isNotEmpty() }
      .map { (filePath, occurrences) -> Match(filePath, occurrences) }
  }

  private fun getOccurrences(
    filePath: String,
    term: String
  ): List<Occurrence> {
    return when {
      term.isBlank() -> listOf(None)
      filePath.toLowerCase().contains(term.toLowerCase()) -> {
        listOf(Segment(filePath.toLowerCase().indexOf(term.toLowerCase()), term.length))
      }
      else -> emptyList()
    }
  }
}
