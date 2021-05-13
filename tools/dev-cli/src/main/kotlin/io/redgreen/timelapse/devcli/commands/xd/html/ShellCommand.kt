package io.redgreen.timelapse.devcli.commands.xd.html

class ShellCommand(private val arguments: Array<String>) {
  fun execute(): ExecutionResult {
    val process = ProcessBuilder()
      .command(*arguments)
      .start()

    return ExecutionResult.from(process)
  }

  override fun toString(): String {
    return arguments.joinToString(" ")
  }
}
