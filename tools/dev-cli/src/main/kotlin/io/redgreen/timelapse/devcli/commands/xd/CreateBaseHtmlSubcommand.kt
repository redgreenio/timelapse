package io.redgreen.timelapse.devcli.commands.xd

import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Spec

@Command(
  name = "xd-html",
  mixinStandardHelpOptions = true,
  description = ["creates a base HTML file for use with Xd"]
)
class CreateBaseHtmlSubcommand : Runnable {
  @Spec
  lateinit var spec: CommandSpec

  override fun run() {
    spec.commandLine().usage(System.out)
  }
}
