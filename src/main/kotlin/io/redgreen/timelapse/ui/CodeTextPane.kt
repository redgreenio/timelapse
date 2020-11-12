package io.redgreen.timelapse.ui

import io.redgreen.timelapse.visuals.DiffLine
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import java.awt.Color
import java.awt.Font
import java.awt.Font.PLAIN
import java.awt.GraphicsEnvironment
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextPane
import javax.swing.KeyStroke
import javax.swing.border.LineBorder
import javax.swing.text.BadLocationException
import javax.swing.text.DefaultCaret
import javax.swing.text.DefaultCaret.NEVER_UPDATE
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import kotlin.LazyThreadSafetyMode.NONE

private const val DEFAULT_CODE_FONT_SIZE = 13.0F
private const val CODE_FONT = "fonts/JetBrainsMono-Regular.ttf"
private const val TITLE_BACKGROUND_COLOR = 0x808080
private const val TITLE_FOREGROUND_COLOR = 0xffffff
private const val TITLE_BORDER_THICKNESS = 10

internal const val KEY_STROKE_UP = "UP"
internal const val KEY_STROKE_DOWN = "DOWN"

private const val ACTION_MAP_KEY_POSITIVE_INCREMENT = "positiveUnitIncrement"
private const val ACTION_MAP_KEY_NEGATIVE_INCREMENT = "negativeUnitIncrement"
private const val KEY_STROKE_LEFT = "LEFT"
private const val KEY_STROKE_RIGHT = "RIGHT"

class TitledCodeTextPane : JPanel(BorderLayout()), DiffDisplay by CodeTextPane() {
  private val titleLabel = JLabel().apply {
    isOpaque = true
    foreground = Color(TITLE_FOREGROUND_COLOR)
    background = Color(TITLE_BACKGROUND_COLOR)
    border = LineBorder(Color(TITLE_BACKGROUND_COLOR), TITLE_BORDER_THICKNESS)
  }

  init {
    add(titleLabel, NORTH)
    add(codeComponent, CENTER)
  }

  fun setTitle(title: String) {
    titleLabel.text = title
  }
}

class CodeTextPane private constructor(private val textPane: JTextPane) : JScrollPane(textPane), DiffDisplay {
  override val codeComponent by lazy { this }

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

    with(getVerticalScrollBar().getInputMap(WHEN_IN_FOCUSED_WINDOW)) {
      put(KeyStroke.getKeyStroke(KEY_STROKE_UP), ACTION_MAP_KEY_NEGATIVE_INCREMENT)
      put(KeyStroke.getKeyStroke(KEY_STROKE_DOWN), ACTION_MAP_KEY_POSITIVE_INCREMENT)
    }

    with(getHorizontalScrollBar().getInputMap(WHEN_IN_FOCUSED_WINDOW)) {
      put(KeyStroke.getKeyStroke(KEY_STROKE_LEFT), ACTION_MAP_KEY_NEGATIVE_INCREMENT)
      put(KeyStroke.getKeyStroke(KEY_STROKE_RIGHT), ACTION_MAP_KEY_POSITIVE_INCREMENT)
    }
  }

  override fun showDiff(diffLines: List<DiffLine>) {
    with(textPane) {
      // Set attributes
      val attributeSet = SimpleAttributeSet()
      val style = this.addStyle(null, null)
      this.setCharacterAttributes(attributeSet, true)
      StyleConstants.setForeground(style, Color.BLACK)

      try {
        // Clear existing text
        this.styledDocument.remove(0, this.document.length)

        // Add new text
        diffLines
          .onEach { span ->
            StyleConstants.setBackground(style, span.backgroundColor())
            styledDocument.insertString(styledDocument.length, span.text(), style)
          }
      } catch (exception: BadLocationException) {
        // Traversing the changed files list with the arrow keys results in
        // this exception. I don't really know how to fix this, so I am swallowing
        // this case until it comes back to haunt me in the night!
      }

      verticalScrollBar.value = 0
      horizontalScrollBar.value = 0
    }
  }
}

interface DiffDisplay {
  val codeComponent: JComponent
  fun showDiff(diffLines: List<DiffLine>)
}
