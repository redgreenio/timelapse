package io.redgreen.javafx

import java.io.InputStream
import javafx.scene.text.Font

object Fonts {
  fun robotoRegular(size: Int): Font =
    Font.loadFont(readFont("Roboto-Regular.ttf"), size.toDouble())

  fun robotoMedium(size: Int): Font =
    Font.loadFont(readFont("Roboto-Medium.ttf"), size.toDouble())

  fun robotoLight(size: Int): Font =
    Font.loadFont(readFont("Roboto-Light.ttf"), size.toDouble())

  private fun readFont(fontFile: String): InputStream {
    return Fonts::class.java
      .getResourceAsStream("/fonts/$fontFile")
  }
}
