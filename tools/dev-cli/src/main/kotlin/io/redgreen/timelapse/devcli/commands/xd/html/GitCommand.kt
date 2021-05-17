package io.redgreen.timelapse.devcli.commands.xd.html

sealed class GitCommand {
  object LsFiles : GitCommand() {
    fun command(fileName: String): ShellCommand =
      ShellCommand(arrayOf("git", "ls-files", fileName, "**/$fileName"))
  }

  object Show : GitCommand() {
    fun command(commitHash: String, filePath: String): ShellCommand =
      ShellCommand(arrayOf("git", "show", "$commitHash:$filePath"))
  }

  object Status : GitCommand() {
    fun command(): ShellCommand =
      ShellCommand(arrayOf("git", "status"))
  }

  object GetUnifiedPatch : GitCommand() {
    fun command(commitHash: String, filePath: String): ShellCommand =
      ShellCommand(arrayOf("git", "diff", "-u", "$commitHash^1", commitHash, "--", filePath))
  }
}
