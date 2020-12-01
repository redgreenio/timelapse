package io.redgreen.timelapse.domain

sealed class BlobDiff {
  data class Simple(val rawDiff: String) : BlobDiff()
}
