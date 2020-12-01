package io.redgreen.timelapse.domain

sealed class BlobDiff {
  data class Simple(
    val parentCommitId: String,
    val rawDiff: String
  ) : BlobDiff()

  data class Merge(
    val diffs: List<Simple>
  ) : BlobDiff()
}
