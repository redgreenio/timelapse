package io.redgreen.timelapse.git.model

import io.sentry.Sentry
import java.io.File
import java.util.Optional
import org.eclipse.jgit.lib.Constants.HEAD
import org.eclipse.jgit.lib.RepositoryBuilder

/* Not using a data class here because it can still expose a private constructor via copy constructors.
 * Therefore, we are also responsible for implementing [#hashCode], [#equals], and [#toString] functions. */
class GitDirectory private constructor(val path: String) {
  companion object {
    fun from(path: String): Optional<GitDirectory> {
      return if (isGitDirectoryCheck(path)) {
        Optional.of(GitDirectory(path))
      } else {
        Optional.empty()
      }
    }

    private fun isGitDirectoryCheck(path: String): Boolean {
      return try {
        RepositoryBuilder().setGitDir(File(path)).build().resolve(HEAD) != null
      } catch (e: Throwable) {
        Sentry.captureException(e, "Git directory check.")
        false
      }
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
