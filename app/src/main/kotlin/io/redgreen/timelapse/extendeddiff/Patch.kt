package io.redgreen.timelapse.extendeddiff

import ext.name.fraser.neil.plaintext.diff_match_patch
import java.util.LinkedList

/* Please refer: https://github.com/google/diff-match-patch/wiki/Unidiff */
private const val ESCAPED_NEWLINE = "%0A"

private const val NEWLINE_CHAR = '\n'
private const val NO_NEW_LINE_INDICATOR_CHAR = '\\'

fun applyPatch(text: String, patch: String): String {
  val diffMatchPatch = diff_match_patch()
  val patchFromText = diffMatchPatch.patch_fromText(formatPatchForDiffMatchPatch(patch))

  val patchApply = diffMatchPatch.patch_apply(LinkedList(patchFromText), text)
  return patchApply.first().toString()
}

private fun formatPatchForDiffMatchPatch(patch: String): String {
  val lines = patch.split(NEWLINE_CHAR)
  return lines
    .mapIndexed { index, line -> escapeNewlines(index, line, lines) }
    .filter { !it.startsWith(NO_NEW_LINE_INDICATOR_CHAR) }
    .joinToString(NEWLINE_CHAR.toString())
}

private fun escapeNewlines(
  index: Int,
  line: String,
  lines: List<String>
): String {
  return if (index == 0 || index == lines.lastIndex || lines[index + 1].startsWith(NO_NEW_LINE_INDICATOR_CHAR)) {
    line
  } else {
    "$line$ESCAPED_NEWLINE"
  }
}
