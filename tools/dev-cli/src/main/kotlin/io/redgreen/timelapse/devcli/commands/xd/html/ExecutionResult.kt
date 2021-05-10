package io.redgreen.timelapse.devcli.commands.xd.html

sealed class ExecutionResult(open val output: String) {
  companion object {
    fun from(process: Process): ExecutionResult {
      val exitCode = process.waitFor()
      val successOutput = process.inputStream.reader().readText().trim()
      val errorOutput = process.errorStream.reader().readText().trim()

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
