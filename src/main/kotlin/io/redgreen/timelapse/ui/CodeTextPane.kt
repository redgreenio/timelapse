package io.redgreen.timelapse.ui

import io.redgreen.timelapse.visuals.DiffSpan
import java.awt.Color
import java.awt.Font
import java.awt.Font.PLAIN
import java.awt.GraphicsEnvironment
import javax.swing.JScrollPane
import javax.swing.JTextPane
import javax.swing.text.DefaultCaret
import javax.swing.text.DefaultCaret.NEVER_UPDATE
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import kotlin.LazyThreadSafetyMode.NONE

private const val DEFAULT_CODE_FONT_SIZE = 13.0F
private const val CODE_FONT = "fonts/JetBrainsMono-Regular.ttf"

class CodeTextPane private constructor(private val textPane: JTextPane) : JScrollPane(textPane) {
  private val codeFont by lazy(NONE) {
    val fontResourceStream = javaClass.classLoader.getResourceAsStream(CODE_FONT)
    val font = Font.createFont(Font.TRUETYPE_FONT, fontResourceStream)
    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font)
    font
  }

  constructor() : this(JTextPane()) {
    with(textPane) {
      font = codeFont.deriveFont(PLAIN, DEFAULT_CODE_FONT_SIZE)
      isEditable = false
      (caret as DefaultCaret).updatePolicy = NEVER_UPDATE
    }
  }

  fun showDiff(diffSpans: List<DiffSpan>) {
    with(textPane) {
      // Clear existing text
      this.styledDocument.remove(0, this.document.length)

      // Add new text
      val attributeSet = SimpleAttributeSet()
      val style = this.addStyle(null, null)
      this.setCharacterAttributes(attributeSet, true)
      StyleConstants.setForeground(style, Color.BLACK)

      diffSpans
        .onEach { span ->
          StyleConstants.setBackground(style, span.backgroundColor())
          styledDocument.insertString(styledDocument.length, span.text(), style)
        }
    }
  }
}
