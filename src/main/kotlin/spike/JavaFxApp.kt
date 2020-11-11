package spike

import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.scene.text.Font
import javafx.scene.web.WebView
import javafx.stage.Stage

fun main(args: Array<String>) {
  launch(JavaFxApp::class.java, *args)
}

class JavaFxApp : Application() {
  override fun start(primaryStage: Stage) {
    val fonts = listOf(
      "/fonts/JetBrainsMono-Regular.ttf",
      "/fonts/FiraCode-Regular.ttf",
    )
    fonts.onEach {
      Font.loadFont(JavaFxApp::class.java.getResource(it).toExternalForm(), 15.0)
    }

    val webView = WebView().apply {
      engine.loadContent("""
        <html>
          <head>
            <style>
              table {
                width: 100%;
                border-collapse: collapse;
              }

              table td {
                font-family: "Fira Code", "JetBrains Mono", "Lucida Console";
                font-size: 15px;
              }

              .deletion {
                background-color: #ffeef0;
              }

              .insertion {
                background-color: #e6ffed;
              }

              .blob {
                padding-left: 10px;
              }

              .diff-section {
                background-color: #f1f8ff;
              }

              .line-number {
                width:1%;
                white-space:nowrap;
                text-align: right;
                vertical-align: top;
                padding: 2px 8px 2px 30px;
                color: rgba(27, 31, 35, 0.4);
              }

              .deletion .line-number {
                background-color: #ffdce0;
              }

              .insertion .line-number {
                background-color: #bef5cb;
              }

              .unmodified .line-number {
                background-color: rgba(230, 230, 230, 0.1);
              }

              .diff-section .line-number {
                background-color: #dbedff;
              }
            </style>
          </head>
          <table>
            <tbody>
              <tr class="diff-section">
                <td class="line-number">​</td><td class="blob"></td>
              </tr>
              <tr class="unmodified">
                <td class="line-number">9</td><td class="blob">&nbsp;&nbsp;fun main() {</td>
              </tr>
              <tr class="deletion">
                <td class="line-number">10</td><td class="blob">-&nbsp;&nbsp;&nbsp;println("Hello, world!")</td>
              </tr>
              <tr class="insertion">
                <td class="line-number">10</td><td class="blob">+&nbsp;&nbsp;&nbsp;println("Hola, mundo!")</td>
              </tr>
              <tr class="unmodified">
                <td class="line-number">11</td><td class="blob">&nbsp;&nbsp;}</td>
              </tr>
            </tbody>
          </table>
        </html>
      """.trimIndent())
    }

    with(primaryStage) {
      title = "Diff Viewer"
      scene = Scene(webView, 800.0, 400.0)
      show()
    }
  }
}
