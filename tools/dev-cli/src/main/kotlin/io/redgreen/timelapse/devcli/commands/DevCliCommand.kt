package io.redgreen.timelapse.devcli.commands

import io.redgreen.timelapse.devcli.commands.xd.CreateBaseHtmlSubcommand
import io.redgreen.timelapse.devcli.commands.xd.CreateXdSampleSubcommand
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File

@Command(
  name = "dev-cli",
  mixinStandardHelpOptions = true,
  subcommands = [CreateBaseHtmlSubcommand::class, CreateXdSampleSubcommand::class],
  description = ["Accelerator for developing Timelapse features."],
  commandListHeading = "%nCommands:%n%nAvailable commands are:%n",
)
class DevCliCommand {
  @Option(names = ["--git-dir"], description = ["Set the path to the repository."])
  private lateinit var gitDir: File
}
