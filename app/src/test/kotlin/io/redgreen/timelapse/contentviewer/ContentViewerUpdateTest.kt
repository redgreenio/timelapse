package io.redgreen.timelapse.contentviewer

import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.NextMatchers.hasNoEffects
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import io.redgreen.timelapse.domain.BlobDiff.Simple
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

  @Test
  fun `when blob diff information is loaded then display it on the screen`() {
    val fileAndRevisionSelected = ContentViewerModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(selectedFilePath, commitId)
    val message = "fix: a long standing issue"
    val blobDiffInformation = BlobDiffInformation(selectedFilePath, commitId, message, 5, 52, 1)

    withUpdateSpec
      .given(fileAndRevisionSelected)
      .whenEvent(BlobDiffInformationLoaded(blobDiffInformation))
      .then(
        assertThatNext(
          hasModel(fileAndRevisionSelected.blobDiffInformationLoaded(blobDiffInformation)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when loading blob diff information fails then show error and retry`() {
    val fileAndRevisionSelected = ContentViewerModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(selectedFilePath, commitId)

    withUpdateSpec
      .given(fileAndRevisionSelected)
      .whenEvent(UnableToLoadBlobDiffInformation)
      .then(
        assertThatNext(
          hasModel(fileAndRevisionSelected.unableToLoadBlobDiffInformation()),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when blob diff is loaded then display it on the screen`() {
    val fileAndRevisionSelected = ContentViewerModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(selectedFilePath, commitId)

    val blobDiff = Simple(
      "parent-commit-id",
      """
          diff --git a/file-4.txt b/file-4.txt
          new file mode 100644
          index 0000000..980a0d5
          --- /dev/null
          +++ b/file-4.txt
          @@ -0,0 +1 @@
          +Hello World!
          
        """.trimIndent()
    )
    withUpdateSpec
      .given(fileAndRevisionSelected)
      .whenEvent(BlobDiffLoaded(blobDiff))
      .then(
        assertThatNext(
          hasModel(fileAndRevisionSelected.blobDiffLoaded(blobDiff)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when loading blob diff fails then show error and retry`() {
    val fileAndRevisionSelected = ContentViewerModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(selectedFilePath, commitId)

    withUpdateSpec
      .given(fileAndRevisionSelected)
      .whenEvent(UnableToLoadBlobDiff)
      .then(
        assertThatNext(
          hasModel(fileAndRevisionSelected.unableToLoadBlobDiff()),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when user clicks retry after loading blob diff information fails, attempt to load blob information again`() {
    val unableToLoadBlobDiffInformation = ContentViewerModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(selectedFilePath, commitId)
      .unableToLoadBlobDiffInformation()

    withUpdateSpec
      .given(unableToLoadBlobDiffInformation)
      .whenEvent(Retry)
      .then(
        assertThatNext(
          hasModel(unableToLoadBlobDiffInformation.loadBlobDiffInformation()),
          hasEffects(LoadBlobDiffInformation(selectedFilePath, commitId))
        )
      )
  }

  @Test
  fun `when user clicks retry after loading blob diff fails, attempt to load blob diff information again`() {
    val unableToLoadBlobDiff = ContentViewerModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(selectedFilePath, commitId)
      .unableToLoadBlobDiff()

    withUpdateSpec
      .given(unableToLoadBlobDiff)
      .whenEvent(Retry)
      .then(
        assertThatNext(
          hasModel(unableToLoadBlobDiff.loadBlobDiff()),
          hasEffects(LoadBlobDiff(selectedFilePath, commitId))
        )
      )
  }

  @Test
  fun `when user clicks on retry after both loading blob diff information and blob diff fails, it should load both`() {
    val unableToLoadBlobDiffAndInformation = ContentViewerModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(selectedFilePath, commitId)
      .unableToLoadBlobDiff()
      .unableToLoadBlobDiffInformation()

    val loadBlobDiffAndInformation = unableToLoadBlobDiffAndInformation
      .loadBlobDiff()
      .loadBlobDiffInformation()
    withUpdateSpec
      .given(unableToLoadBlobDiffAndInformation)
      .whenEvent(Retry)
      .then(
        assertThatNext(
          hasModel(loadBlobDiffAndInformation),
          hasEffects(LoadBlobDiffInformation(selectedFilePath, commitId), LoadBlobDiff(selectedFilePath, commitId))
        )
      )
  }
}
