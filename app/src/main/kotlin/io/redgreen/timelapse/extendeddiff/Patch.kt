package io.redgreen.timelapse.extendeddiff

import com.cloudbees.diff.ContextualPatch
import java.io.File

internal fun applyPatch(text: String, patch: String): String {
  val patchFile = File.createTempFile("patch", ".patch").apply { writeText(patch) }
  val textFile = File.createTempFile("text", ".source").apply { writeText(text) }
  val create = ContextualPatch.create(patchFile, textFile)
  create.patch(false)
  return textFile.readText()
}
