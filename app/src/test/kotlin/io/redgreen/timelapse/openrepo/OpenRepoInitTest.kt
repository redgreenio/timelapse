package io.redgreen.timelapse.openrepo

import com.spotify.mobius.test.FirstMatchers.hasEffects
import com.spotify.mobius.test.FirstMatchers.hasModel
import com.spotify.mobius.test.InitSpec.assertThatFirst
import io.redgreen.timelapse.mobius.spec
import org.junit.jupiter.api.Test

class OpenRepoInitTest {
  @Test
  fun `when the user opens the application, then get the user's name and their recent repositories`() {
    val start = OpenRepoModel.start()

    OpenRepoInit
      .spec()
      .whenInit(start)
      .then(
        assertThatFirst(
          hasModel(start),
          hasEffects(FindGitUsername, GetRecentRepositories)
        )
      )
  }
}
