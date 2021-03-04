package io.redgreen.timelapse.core

import java.io.File
import java.util.Optional

/* Not using a sealed class here because it can still expose a private constructor via copy constructors. Therefore,
 * we are also responsible for implementing [#hashCode], [#equals], and [#toString] functions. */
class GitDirectory private constructor(val path: String) {
  companion object {
    private const val GIT_DIRECTORY_NAME = ".git"

    fun from(path: String): Optional<GitDirectory> {
      return if (isGitDirectoryNaiveCheck(path)) {
        Optional.of(GitDirectory(path))
      } else {
        Optional.empty()
      }
    }

    private fun isGitDirectoryNaiveCheck(path: String): Boolean {
      val possibleGitDirectory = File(path)
      val isDirectoryPath = possibleGitDirectory.exists() && possibleGitDirectory.isDirectory
      val isGitDirectory = possibleGitDirectory.name == GIT_DIRECTORY_NAME
      return isDirectoryPath && isGitDirectory
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as GitDirectory

    if (path != other.path) return false

    return true
  }

  override fun hashCode(): Int {
    return path.hashCode()
  }

  override fun toString(): String {
    return "GitDirectory(path='$path')"
  }
}
