package io.redgreen.timelapse.extendeddiff

import ext.name.fraser.neil.plaintext.diff_match_patch
import java.util.LinkedList

fun applyPatch(text: String, patch: String): String {
  val diffMatchPatch = diff_match_patch()
  val patchFromText = diffMatchPatch.patch_fromText(patch)

  val patchApply = diffMatchPatch.patch_apply(LinkedList(patchFromText), text)
  return patchApply.first().toString()
}
