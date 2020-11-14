package spike

import io.redgreen.timelapse.diff.DiffViewer
import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.scene.text.Font
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

    val htmlContent = """
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
      
            table tr td:first-child {
              padding-left: 24px;
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
              padding: 2px 8px 2px 8px;
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
              <td class="line-number">​</td><td class="line-number">​</td><td class="blob"></td>
            </tr>
            <tr class="unmodified">
              <td class="line-number">1</td><td class="line-number">1</td><td class="blob">&nbsp;buildscript {</td>
            </tr>
            <tr class="unmodified">
              <td class="line-number">2</td><td class="line-number">2</td><td class="blob">&nbsp;&nbsp;&nbsp;apply from: 'buildscripts/plugins.gradle'</td>
            </tr>
            <tr class="deletion">
              <td class="line-number">3</td><td class="line-number"></td><td class="blob">-&nbsp;&nbsp;ext.kotlin_version = '1.3.70'</td>
            </tr>
            <tr class="insertion">
              <td class="line-number"></td><td class="line-number">3</td><td class="blob">+&nbsp;&nbsp;ext.kotlin_version = '1.3.72'</td>
            </tr>
            <tr class="unmodified">
              <td class="line-number">4</td><td class="line-number">4</td><td class="blob">&nbsp;</td>
            </tr>
            <tr class="unmodified">
              <td class="line-number">5</td><td class="line-number">5</td><td class="blob">&nbsp;&nbsp;&nbsp;repositories {</td>
            </tr>
            <tr class="unmodified">
              <td class="line-number">6</td><td class="line-number">6</td><td class="blob">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mavenCentral()</td>
            </tr>
          </tbody>
        </table>
      </html>
      """.trimIndent()

    val diffViewer = DiffViewer()

    with(primaryStage) {
      title = "Diff Viewer"
      scene = Scene(diffViewer, 800.0, 400.0)
      show()
    }
  }
}
