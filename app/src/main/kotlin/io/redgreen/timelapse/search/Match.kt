package io.redgreen.timelapse.search

data class Match(
  val text: String,

  /**
   * The default parameter represents the special case with an empty search term (""). We use it as a marker to
   * not highlight any text in the @see Match for an empty search term.
   **/
  val occurrences: List<Occurrence> = listOf(Occurrence.None)
)
