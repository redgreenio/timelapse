package io.redgreen.timelapse.git.model

/** Represents a file path tracked by Git. */
inline class TrackedFilePath(val value: String) {
  val filename: String
    get() = value.substring(value.lastIndexOf('/') + 1)
}
