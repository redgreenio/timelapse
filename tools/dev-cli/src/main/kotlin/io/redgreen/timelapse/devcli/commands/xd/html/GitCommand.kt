package io.redgreen.timelapse.devcli.commands.xd.html

sealed class GitCommand {
  object LsFiles : GitCommand() {
    fun command(fileName: String): Command =
      Command(arrayOf("git", "ls-files", fileName, "**/$fileName"))
  }

  object Show : GitCommand() {
    fun command(commitHash: String, filePath: String): Command =
      Command(arrayOf("git", "show", "$commitHash:$filePath"))
  }

  object Status : GitCommand() {
    fun command(): Command =
      Command(arrayOf("git", "status"))
  }
}
