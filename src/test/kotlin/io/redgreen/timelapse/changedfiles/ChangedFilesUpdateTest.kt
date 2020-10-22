package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import io.redgreen.timelapse.changedfiles.ChangedFilesModel.HasSelection
import io.redgreen.timelapse.changedfiles.ChangedFilesModel.NoSelection
import io.redgreen.timelapse.mobius.spec
import org.junit.jupiter.api.Test

class ChangedFilesUpdateTest {
  private val withUpdateSpec = ChangedFilesUpdate.spec()

  @Test
  fun `when user selects a revision it should show loading state`() {
    val noSelectionState = NoSelection
    val commitId = "commit-id"
    val filePath = "app/build.gradle"

    withUpdateSpec
      .given(noSelectionState)
      .whenEvent(RevisionSelected(commitId, filePath))
      .then(
        assertThatNext(
          hasModel(HasSelection(commitId, filePath, emptyList())),
          hasEffects(FetchChangedFiles(commitId, filePath))
        )
      )
  }

  @Test
  fun `when there are no other changed files, it should return none`() {
    val commitId = "commit-id"
    val filePath = "app/build.gradle"
    val revisionSelectedState = NoSelection
      .revisionSelected(commitId, filePath)
    val changedFiles = emptyList<String>()

    withUpdateSpec
      .given(revisionSelectedState)
      .whenEvent(NoOtherFilesChanged)
      .then(
        assertThatNext(
          hasModel(HasSelection(commitId, filePath, changedFiles))
        )
      )
  }
}
