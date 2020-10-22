package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.NextMatchers.hasNoEffects
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import io.redgreen.timelapse.changedfiles.ChangedFilesModel.HasSelection
import io.redgreen.timelapse.changedfiles.ChangedFilesModel.NoSelection
import io.redgreen.timelapse.mobius.spec
import org.junit.jupiter.api.Test

class ChangedFilesUpdateTest {
  private val withUpdateSpec = ChangedFilesUpdate.spec()
  private val commitId = "commit-id"
  private val selectedFilePath = "app/build.gradle"

  @Test
  fun `when user selects a revision it should show loading state`() {
    val noSelectionState = NoSelection

    withUpdateSpec
      .given(noSelectionState)
      .whenEvent(RevisionSelected(commitId, selectedFilePath))
      .then(
        assertThatNext(
          hasModel(HasSelection(commitId, selectedFilePath, emptyList())),
          hasEffects(FetchChangedFiles(commitId, selectedFilePath))
        )
      )
  }

  @Test
  fun `when there are no other changed files, it should show not other files changed`() {
    val revisionSelectedState = NoSelection
      .revisionSelected(commitId, selectedFilePath)
    val noChangedFiles = emptyList<String>()

    withUpdateSpec
      .given(revisionSelectedState)
      .whenEvent(NoOtherFilesChanged)
      .then(
        assertThatNext(
          hasModel(HasSelection(commitId, selectedFilePath, noChangedFiles)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when there are some changed files, it should display those changed files`() {
    val revisionSelectedState = NoSelection
      .revisionSelected(commitId, selectedFilePath)
    val changedFiles = listOf("build.gradle", "settings.gradle")

    withUpdateSpec
      .given(revisionSelectedState)
      .whenEvent(SomeFilesChanged(changedFiles))
      .then(
        assertThatNext(
          hasModel(HasSelection(commitId, selectedFilePath, changedFiles)),
          hasNoEffects()
        )
      )
  }
}
