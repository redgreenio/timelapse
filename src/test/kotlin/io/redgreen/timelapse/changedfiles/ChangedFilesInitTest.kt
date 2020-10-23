package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.test.FirstMatchers.hasModel
import com.spotify.mobius.test.FirstMatchers.hasNoEffects
import com.spotify.mobius.test.InitSpec.assertThatFirst
import io.redgreen.timelapse.mobius.AsyncOp.Companion.idle
import io.redgreen.timelapse.mobius.spec
import org.junit.jupiter.api.Test

class ChangedFilesInitTest {
  @Test
  fun `when user has not selected a file and a revision, then show no file and revision selected`() {
    ChangedFilesInit
      .spec()
      .whenInit(ChangedFilesModel.noFileAndRevisionSelected())
      .then(
        assertThatFirst(
          hasModel(ChangedFilesModel(null, null, idle())),
          hasNoEffects()
        )
      )
  }
}
