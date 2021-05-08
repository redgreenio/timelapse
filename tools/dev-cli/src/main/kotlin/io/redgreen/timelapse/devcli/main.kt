package io.redgreen.timelapse.devcli

import io.redgreen.timelapse.devcli.commands.DevCliCommand
import org.fusesource.jansi.AnsiConsole
import picocli.CommandLine

fun main(args: Array<String>) {
  AnsiConsole.systemInstall()
  CommandLine(DevCliCommand()).execute(*args)
  AnsiConsole.systemUninstall()
}
