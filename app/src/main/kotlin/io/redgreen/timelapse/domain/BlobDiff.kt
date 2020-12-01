package io.redgreen.timelapse.domain

sealed class BlobDiff {
  /* Diff representing commits with a single parent. */
  data class Simple(
    val parentCommitId: String,
    val rawDiff: String
  ) : BlobDiff()

  /* Diff representing commits with more than one parents. */
  data class Merge(
    val diffs: List<Simple>
  ) : BlobDiff()
}
