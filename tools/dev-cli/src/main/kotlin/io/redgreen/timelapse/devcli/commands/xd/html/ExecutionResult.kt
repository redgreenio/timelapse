package io.redgreen.timelapse.devcli.commands.xd.html

sealed class ExecutionResult(open val output: String) {
  companion object {
    fun from(process: Process): ExecutionResult {
      val successOutput = process.inputStream.reader().readText()
      val errorOutput = process.errorStream.reader().readText()
      val exitCode = process.exitValue()

      return if (exitCode == 0) {
        Success(successOutput)
      } else {
        Failure(exitCode, errorOutput)
      }
    }
  }

  data class Success(override val output: String) : ExecutionResult(output)
  data class Failure(val exitCode: Int, override val output: String) : ExecutionResult(output)
}
