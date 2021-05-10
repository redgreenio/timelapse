package io.redgreen.timelapse.devcli.commands.xd.html

sealed class GitCommand {
  object LsFiles : GitCommand() {
    fun command(fileName: String): Array<String> =
      arrayOf("git", "ls-files", fileName, "**/$fileName")
  }

  object Show : GitCommand() {
    fun command(commitHash: String, filePath: String): Array<String> =
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
