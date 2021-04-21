package io.redgreen.timelapse.extendeddiff

import ext.name.fraser.neil.plaintext.diff_match_patch
import java.util.LinkedList

/* Please refer: https://github.com/google/diff-match-patch/wiki/Unidiff */
private const val ESCAPED_NEWLINE = "%0A"

private const val NEWLINE_CHAR = '\n'
private const val PATCH_HEADER_CHAR = '@'
private const val NO_NEW_LINE_INDICATOR_CHAR = '\\'

internal fun applyPatch(text: String, patch: String): String {
  val diffMatchPatch = diff_match_patch()
  val patchFromText = diffMatchPatch.patch_fromText(formatPatchForDiffMatchPatch(patch))

  val patchApply = diffMatchPatch.patch_apply(LinkedList(patchFromText), text)
  return patchApply.first().toString()
}

private fun formatPatchForDiffMatchPatch(patch: String): String {
  val lines = sanitize(patch).split(NEWLINE_CHAR)
  return lines
    .mapIndexed { index, line -> escapeNewlines(index, line, lines) }
    .filter { !it.startsWith(NO_NEW_LINE_INDICATOR_CHAR) }
    .joinToString(NEWLINE_CHAR.toString())
}

private fun sanitize(patch: String): String {
  if (patch.isEmpty()) {
    return patch
  }

  val lines = patch
    .split(NEWLINE_CHAR)

  val fileHeaderLinesLength = lines
    .indexOfFirst { it.startsWith(PATCH_HEADER_CHAR) }

  return lines
    .drop(fileHeaderLinesLength)
    .joinToString(NEWLINE_CHAR.toString())
}

private fun escapeNewlines(
  index: Int,
  line: String,
  lines: List<String>
): String {
  val noNewlineRequired = line.startsWith(PATCH_HEADER_CHAR) ||
    index == lines.lastIndex ||
    lines[index + 1].startsWith(NO_NEW_LINE_INDICATOR_CHAR)
  return if (noNewlineRequired) {
    line
  } else {
    "$line$ESCAPED_NEWLINE"
  }
}
