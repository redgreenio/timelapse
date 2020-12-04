package io.redgreen.timelapse.platform

import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent

class JavaFxClipboardService : ClipboardService {
  override fun copy(text: String) {
    val clipboard = Clipboard.getSystemClipboard()
    val clipboardContent = ClipboardContent().apply { putString(text) }
    clipboard.setContent(clipboardContent)
  }
}
