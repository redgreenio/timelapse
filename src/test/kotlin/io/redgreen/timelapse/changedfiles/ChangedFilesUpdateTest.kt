package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import io.redgreen.timelapse.changedfiles.ChangedFilesModel.HasSelection
import io.redgreen.timelapse.changedfiles.ChangedFilesModel.NoSelection
import io.redgreen.timelapse.mobius.spec
import org.junit.jupiter.api.Test

class ChangedFilesUpdateTest {
  @Test
  fun `when user selects a revision it should show loading state`() {
    val currentState = NoSelection

    ChangedFilesUpdate
      .spec()
      .given(currentState)
      .whenEvent(RevisionSelected("commit-id", "file-path"))
      .then(
        assertThatNext(
          hasModel(HasSelection("commit-id", "file-path")),
          hasEffects(FetchChangedFiles("commit-id", "file-path"))
        )
      )
  }
}
