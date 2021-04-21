package io.redgreen.timelapse.extendeddiff

fun ExtendedDiff.toHtml(): String {
  return """
    <html lang="en-US">
    <head>
        <style>
          .added {
            background-color: #e6ffed;
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
