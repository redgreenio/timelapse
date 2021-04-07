package io.redgreen.timelapse.foo

import java.io.File

private const val GIT_DIRECTORY_NAME = ".git"

fun String.appendDotGit(): String {
  return if (this.endsWith(File.separatorChar)) {
    "$this$GIT_DIRECTORY_NAME"
  } else {
    "$this${File.separator}$GIT_DIRECTORY_NAME"
  }
}
