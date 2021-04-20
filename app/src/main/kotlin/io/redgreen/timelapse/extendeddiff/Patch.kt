package io.redgreen.timelapse.extendeddiff

import ext.name.fraser.neil.plaintext.diff_match_patch
import java.util.LinkedList

/* Please refer: https://github.com/google/diff-match-patch/wiki/Unidiff */
private const val ESCAPED_NEWLINE = "%0A"

private const val INSERTION_CHAR = '+'
private const val NEWLINE_CHAR = '\n'

fun applyPatch(text: String, patch: String): String {
  val diffMatchPatch = diff_match_patch()
  val patchFromText = diffMatchPatch.patch_fromText(formatPatchForDiffMatchPatch(patch))

  val patchApply = diffMatchPatch.patch_apply(LinkedList(patchFromText), text)
  return patchApply.first().toString()
}

private fun formatPatchForDiffMatchPatch(patch: String): String {
  val lines = patch.split(NEWLINE_CHAR)

  return lines
    .mapIndexed { index, line ->
      if (line.startsWith(INSERTION_CHAR) && index != lines.lastIndex) {
        "$line$ESCAPED_NEWLINE"
      } else {
        line
      }
    }
    .joinToString(NEWLINE_CHAR.toString())
}
