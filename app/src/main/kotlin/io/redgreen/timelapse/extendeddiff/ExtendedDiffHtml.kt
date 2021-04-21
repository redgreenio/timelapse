package io.redgreen.timelapse.extendeddiff

import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges

@SuppressWarnings("LongMethod")
fun ExtendedDiff.toHtml(): String {
  val isAdded = this is HasChanges && this.comparisonResults.first() is Added
  val isDeletion = this is HasChanges && this.comparisonResults.first() is Deleted

  return when {
    isAdded -> {
      """
        <html lang="en-US">
        <head>
            <style>
              .added {
                background-color: #e6ffed;
              }
              .modified {
                background-color: #dbedff80;
              }
              .deleted {
                background-color: #ffdce0;
              }
            </style>
        </head>
        <body>
        <table>
            <tbody>
            <tr>
                <td>1</td>
                <td>fun a() {}</td>
            </tr>
            <tr class="added">
                <td>2</td>
                <td>fun b() {}</td>
            </tr>
            </tbody>
        </table>
        </body>
        </html>
      """.trimIndent()
    }
    isDeletion -> {
      """
        <html lang="en-US">
        <head>
            <style>
              .added {
                background-color: #e6ffed;
              }
              .modified {
                background-color: #dbedff80;
              }
              .deleted {
                background-color: #ffdce0;
              }
            </style>
        </head>
        <body>
        <table>
            <tbody>
            <tr class="deleted">
                <td></td>
                <td>fun a() {</td>
            </tr>
            <tr class="deleted">
                <td></td>
                <td>}</td>
            </tr>
            <tr>
                <td>1</td>
                <td>fun b() {</td>
            </tr>
            <tr>
                <td>2</td>
                <td>}</td>
            </tr>
            </tbody>
        </table>
        </body>
        </html>
      """.trimIndent()
    }
    else -> {
      """
        <html lang="en-US">
        <head>
            <style>
              .added {
                background-color: #e6ffed;
              }
              .modified {
                background-color: #dbedff80;
              }
              .deleted {
                background-color: #ffdce0;
              }
            </style>
        </head>
        <body>
        <table>
            <tbody>
            <tr>
                <td>1</td>
                <td>class SomeClass {</td>
            </tr>
            <tr class="modified">
                <td>2</td>
                <td>&nbsp;&nbsp;func a() {</td>
            </tr>
            <tr class="modified">
                <td>3</td>
                <td>&nbsp;&nbsp;&nbsp;&nbsp;// Hello, world!</td>
            </tr>
            <tr class="modified">
                <td>4</td>
                <td>&nbsp;&nbsp;}</td>
            </tr>
            <tr>
                <td>5</td>
                <td>}</td>
            </tr>
            </tbody>
        </table>
        </body>
        </html>
      """.trimIndent()
    }
  }
}
