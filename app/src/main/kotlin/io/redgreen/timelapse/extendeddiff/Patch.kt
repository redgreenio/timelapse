package io.redgreen.timelapse.extendeddiff

import com.cloudbees.diff.ContextualPatch
import java.io.File

private const val PATCH_FILE_PREFIX = "patch"
private const val PATCH_FILE_SUFFIX = ".pat"

private const val SOURCE_FILE_PREFIX = "source"
private const val SOURCE_FILE_SUFFIX = ".src"

private const val BACKUP_SOURCE_FILE_EXTENSION = ".original~"

internal fun applyPatch(source: String, patch: String): String {
  val sourceFile = newSourceFile(source)
  applyPatch(sourceFile, patch)
  setupBackedUpSourceFileForCleanup(sourceFile)
  return sourceFile.readText()
}

private fun newSourceFile(source: String): File {
  return File.createTempFile(SOURCE_FILE_PREFIX, SOURCE_FILE_SUFFIX).apply { deleteOnExit(); writeText(source) }
}

private fun applyPatch(sourceFile: File, patch: String) {
  val patchFile = newPatchFile(patch)
  ContextualPatch.create(patchFile, sourceFile).apply { patch(false) }
}

private fun newPatchFile(patch: String): File {
  return File.createTempFile(PATCH_FILE_PREFIX, PATCH_FILE_SUFFIX).apply { deleteOnExit(); writeText(patch) }
}

private fun setupBackedUpSourceFileForCleanup(sourceFile: File) {
  val backedUpSourceFile = File("${sourceFile.canonicalPath}$BACKUP_SOURCE_FILE_EXTENSION")
  if (backedUpSourceFile.exists()) {
    backedUpSourceFile.deleteOnExit()
  }
}
