package io.redgreen.timelapse.visuals

import java.text.SimpleDateFormat
import java.util.Date

private val dateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm")

fun formatDate(
  authoredDate: Date,
  committedDate: Date
): String =
  "${dateFormat.format(authoredDate)}, committed on ${dateFormat.format(committedDate)}"
