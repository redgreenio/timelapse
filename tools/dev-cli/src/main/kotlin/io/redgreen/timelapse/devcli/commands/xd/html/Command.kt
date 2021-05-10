package io.redgreen.timelapse.devcli.commands.xd.html

class Command(private val arguments: Array<String>) {
  fun execute(): ExecutionResult {
    val process = ProcessBuilder()
      .command(*arguments)
      .start()

    return ExecutionResult.from(process)
  }
}
