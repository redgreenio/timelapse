package io.redgreen.timelapse.openrepo.data

sealed class GitUsername {
  object NotFound : GitUsername() {
    override fun toString(): String =
      NotFound::class.java.simpleName
  }

  data class Found(
    val username: String
  ) : GitUsername()
}
