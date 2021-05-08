package io.redgreen.timelapse.devcli

import io.redgreen.timelapse.devcli.commands.DevCliCommand
import picocli.CommandLine

fun main(args: Array<String>) {
  CommandLine(DevCliCommand()).execute(*args)
}
