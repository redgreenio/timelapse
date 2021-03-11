package io.redgreen.timelapse.openrepo

import io.redgreen.timelapse.git.model.GitDirectory
import java.util.Optional
import org.eclipse.jgit.util.SystemReader

class GitDetector {
  companion object {
    private const val SECTION_USER = "user"
    private const val KEY_NAME = "name"
  }

  fun globalUsername(): Optional<String> {
    val maybeUsername = SystemReader.getInstance().userConfig.getString(SECTION_USER, null, KEY_NAME)
    return if (!maybeUsername.isNullOrBlank()) {
      Optional.of(maybeUsername)
    } else {
      Optional.empty()
    }
  }

  @Suppress("DeprecatedCallableAddReplaceWith")
  @Deprecated("Use [GitDirectory] instead")
  fun isGitRepository(path: String): Boolean =
    GitDirectory.from(path).isPresent
}
