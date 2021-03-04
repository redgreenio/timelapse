package io.redgreen.timelapse.core

import java.io.File
import java.util.Optional

class GitDirectory(val path: String) {
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
}
