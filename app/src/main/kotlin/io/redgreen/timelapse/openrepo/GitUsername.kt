package io.redgreen.timelapse.openrepo

sealed class GitUsername {
  object NotFound : GitUsername() {
    override fun toString(): String =
      NotFound::class.simpleName!!
  }

  data class Found(
    val username: String
  ) : GitUsername()
}
