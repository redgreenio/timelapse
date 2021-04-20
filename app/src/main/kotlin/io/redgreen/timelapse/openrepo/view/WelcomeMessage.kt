package io.redgreen.timelapse.openrepo.view

import io.redgreen.timelapse.foo.fastLazy

sealed class WelcomeMessage {
  object Stranger : WelcomeMessage()

  data class Greeter(
    val username: String
  ) : WelcomeMessage() {
    val displayName: String by fastLazy {
      username.trim().split(' ').first()
    }
  }
}
