package io.redgreen.timelapse

import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE

fun main(args: Array<String>) {
  debug = args.find { it == "--debug" }?.let { true } ?: false

  val width = 800
  val height = 400
  val insertions = listOf(
   3, 8, 1, 3, 1, 1, 2, 2, 3, 2, 1, 1, 11, 5, 11, 31, 6, 6, 1, 2, 14,
   1, 1, 7, 1, 1, 1, 1, 2, 2, 4, 2, 4, 2, 1, 1, 2, 1, 11, 8, 6, 12, 10,
  )

  JFrame("Timelapse").apply {
    defaultCloseOperation = EXIT_ON_CLOSE
    setSize(width, height)
    setLocationRelativeTo(null)
    contentPane.add(AreaChart().apply { this.commits = insertions.map(::Commit) })
    isVisible = true
  }
}
