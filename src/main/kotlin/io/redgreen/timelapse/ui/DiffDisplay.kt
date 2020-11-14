package io.redgreen.timelapse.ui

import io.redgreen.timelapse.diff.FormattedDiff
import javax.swing.JComponent

interface DiffDisplay {
  val codeComponent: JComponent
  fun showDiff(diff: FormattedDiff)
}
