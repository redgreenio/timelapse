package io.redgreen.timelapse.devcli.commands.xd.html

sealed class GitCommand {
  object LsFiles : GitCommand() {
    // git ls-files ExtendedDiffHtml.kt '**/ExtendedDiffHtml.kt'
    fun from(fileName: String): Array<String> =
      arrayOf("git", "ls-files", fileName, "**/$fileName")
  }

  // git show 1f69e11d..66273236:app/src/main/kotlin/io/redgreen/timelapse/extendeddiff/ExtendedDiffHtml.kt
  object Show : GitCommand() {
    fun from(commitHash: String, filePath: String): Array<String> =
      arrayOf("git", "show", "$commitHash:$filePath")
  }

  object Status : GitCommand() {
    fun command(): Array<String> =
      arrayOf("git", "status")
  }
}

internal fun Array<String>.execute(): ExecutionResult {
  val process = ProcessBuilder()
    .command(*this)
    .start()

  return ExecutionResult.from(process)
}
