package io.redgreen.timelapse.devcli.commands

import io.redgreen.timelapse.devcli.commands.xd.CreateXdSampleSubcommand
import io.redgreen.timelapse.devcli.commands.xd.html.CreateBaseHtmlSubcommand
import picocli.CommandLine.Command

@Command(
  name = "dev-cli",
  mixinStandardHelpOptions = true,
  subcommands = [CreateBaseHtmlSubcommand::class, CreateXdSampleSubcommand::class],
  description = ["Accelerator for developing Timelapse features."],
  commandListHeading = "%nCommands:%n%nAvailable commands are:%n",
  version = ["v0.0.5"],
)
class DevCliCommand
