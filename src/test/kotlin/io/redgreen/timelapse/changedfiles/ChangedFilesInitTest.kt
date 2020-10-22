package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.test.FirstMatchers.hasModel
import com.spotify.mobius.test.FirstMatchers.hasNoEffects
import com.spotify.mobius.test.InitSpec
import com.spotify.mobius.test.InitSpec.assertThatFirst
import io.redgreen.timelapse.changedfiles.ChangedFilesModel.NoSelection
import org.junit.jupiter.api.Test

class ChangedFilesInitTest {
  @Test
  fun `when user has not selected a revision, then show nothing selected`() {
    InitSpec(ChangedFilesInit())
      .whenInit(NoSelection)
      .then(
        assertThatFirst(
          hasModel(NoSelection),
          hasNoEffects()
        )
      )
  }
}
