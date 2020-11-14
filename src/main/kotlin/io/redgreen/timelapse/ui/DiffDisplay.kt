package io.redgreen.timelapse.ui

import io.redgreen.timelapse.diff.DiffLine
import javax.swing.JComponent

interface DiffDisplay {
  val codeComponent: JComponent
  fun showDiff(diffLines: List<DiffLine>)
}
