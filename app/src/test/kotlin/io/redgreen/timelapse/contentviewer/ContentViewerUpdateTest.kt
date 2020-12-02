package io.redgreen.timelapse.contentviewer

import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import io.redgreen.timelapse.mobius.spec
import org.junit.jupiter.api.Test

class ContentViewerUpdateTest {
  private val withUpdateSpec = ContentViewerUpdate.spec()
  private val commitId = "commit-id"
  private val selectedFilePath = "CHANGELOG.md"

  @Test
  fun `when user selects a file path and revision it should begin loading file information and the blob diff`() {
    val noFileAndRevisionSelected = ContentViewerModel
      .noFileAndRevisionSelected()

    withUpdateSpec
      .given(noFileAndRevisionSelected)
      .whenEvent(FileAndRevisionSelected(selectedFilePath, commitId))
      .then(
        assertThatNext(
          hasModel(noFileAndRevisionSelected.fileAndRevisionSelected(selectedFilePath, commitId)),
          hasEffects(
            LoadBlobDiffInformation(selectedFilePath, commitId),
            LoadBlobDiff(selectedFilePath, commitId)
          )
        )
      )
  }
}
