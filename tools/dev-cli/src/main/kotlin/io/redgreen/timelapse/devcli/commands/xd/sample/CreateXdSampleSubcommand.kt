package io.redgreen.timelapse.devcli.commands.xd.sample

import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Spec

@Command(
  name = "sample",
  mixinStandardHelpOptions = true,
  description = ["creates seed and patch files for Xd sample"],
  hidden = true
)
class CreateXdSampleSubcommand : Runnable {
  @Spec
  lateinit var spec: CommandSpec

  override fun run() {
    spec.commandLine().usage(System.out)
  }
}
