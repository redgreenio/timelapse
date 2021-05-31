package io.redgreen.timelapse.dev.actions

import com.intellij.openapi.vfs.VirtualFile

private const val WINDOWS_NEWLINE = "\r\n"
private const val POSIX_NEWLINE = "\n"

fun VirtualFile.readText(): String {
  return inputStream
    .reader()
    .readText()
    .replace(WINDOWS_NEWLINE, POSIX_NEWLINE)
}
