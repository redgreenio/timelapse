package io.redgreen.timelapse.extensions

import org.eclipse.jgit.diff.Edit

/* Implemented using information from [Edit] documentation. */
fun Edit.isInsert(): Boolean =
  beginA == endA && beginB < endB

fun Edit.isDelete(): Boolean =
  beginA < endA && beginB == endB

fun Edit.isReplace(): Boolean =
  beginA < endA && beginB < endB
