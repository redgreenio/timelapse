package io.redgreen.timelapse.openrepo

import org.eclipse.jgit.util.SystemReader
import java.util.Optional

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
}
