package io.redgreen.timelapse.devcli.commands

import io.redgreen.timelapse.devcli.commands.xd.html.CreateBaseHtmlSubcommand
import io.redgreen.timelapse.devcli.commands.xd.sample.CreateXdSampleSubcommand
import picocli.CommandLine.Command

internal const val TOOL_VERSION = "0.0.16-dev"

@Command(
  name = "dev-cli",
  mixinStandardHelpOptions = true,
  subcommands = [CreateBaseHtmlSubcommand::class, CreateXdSampleSubcommand::class],
  description = ["Accelerator for developing Timelapse features."],
  commandListHeading = "%nCommands:%n%nAvailable commands are:%n",
  version = ["v$TOOL_VERSION"],
)
class DevCliCommand
