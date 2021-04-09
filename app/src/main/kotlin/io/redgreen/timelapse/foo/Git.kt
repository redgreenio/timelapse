package io.redgreen.timelapse.foo

import io.redgreen.timelapse.git.model.GitDirectory
import java.io.File

private const val GIT_DIRECTORY_NAME = ".git"

val IS_GIT_REPOSITORY_PREDICATE: (path: String) -> Boolean = { GitDirectory.from(it).isPresent }

fun String.appendDotGit(): String {
  return if (this.endsWith(File.separatorChar)) {
    "$this$GIT_DIRECTORY_NAME"
  } else {
    "$this${File.separator}$GIT_DIRECTORY_NAME"
  }
}
