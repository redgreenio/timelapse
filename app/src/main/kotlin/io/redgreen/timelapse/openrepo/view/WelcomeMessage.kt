package io.redgreen.timelapse.openrepo.view

sealed class WelcomeMessage {
  object Stranger : WelcomeMessage()
  data class Greeter(
    val username: String
  ) : WelcomeMessage()
}
