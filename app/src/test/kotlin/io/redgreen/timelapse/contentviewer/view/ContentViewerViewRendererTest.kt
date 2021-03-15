package io.redgreen.timelapse.contentviewer.view

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.redgreen.timelapse.contentviewer.ContentViewerModel
import io.redgreen.timelapse.contentviewer.data.BlobDiffInformation
import io.redgreen.timelapse.domain.BlobDiff.Simple
import org.junit.jupiter.api.Test

class ContentViewerViewRendererTest {
  private val view = mock<ContentViewerView>()
  private val viewRenderer = ContentViewerViewRenderer(view)

  @Test
  fun `it should render nothing selected`() {
    // given
    val noFileAndRevisionSelected = ContentViewerModel
      .noFileAndRevisionSelected()

    // when
    viewRenderer.render(noFileAndRevisionSelected)

    // then
    verify(view).fileNameLabelVisible(false)
    verify(view).deletionsInsertionsAndFilesChangedLabelsVisible(false)
    verify(view).commitIdLabelVisible(false)
    verify(view).commitMessageLabelVisible(false)
    verify(view).displaySelectFileMessage()
    verifyNoMoreInteractions(view)
  }

  @Test
  fun `it should render loading blob diff`() {
    // given
    val selectedFilePath = "file-1.txt"
    val commitId = "27873ea62687e72d736aca160047cee05142fcae"

    val fileAndRevisionSelected = ContentViewerModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(selectedFilePath, commitId)

    // when
    viewRenderer.render(fileAndRevisionSelected)

    // then
    verify(view).fileNameLabelVisible(true)
    verify(view).deletionsInsertionsAndFilesChangedLabelsVisible(true)
    verify(view).commitIdLabelVisible(true)
    verify(view).setFileName(selectedFilePath)
    verify(view).setCommitId(commitId)
    verify(view).displayLoadingMessage()
    verifyNoMoreInteractions(view)
  }

  @Test
  fun `it should render blob diff loading successful`() {
    // given
    val selectedFilePath = "file-1.txt"
    val commitId = "27873ea62687e72d736aca160047cee05142fcae"
    val message = "exhibit c: modify a file"
    val deletions = 0
    val insertions = 1
    val changedFiles = 1

    val rawDiff = """
          diff --git a/file-b.txt b/file-b.txt
          new file mode 100644
          index 0000000..e69de29
          --- /dev/null
          +++ b/file-1.txt
          @@ -0,0 +1 @@
          +Hello World!
          
    """.trimIndent()

    val blobDiffInformation = BlobDiffInformation(
      selectedFilePath,
      commitId,
      message,
      deletions,
      insertions,
      changedFiles
    )

    val blobDiff = Simple("6c2faf72204d1848bdaef44f4e69c2c4ae6ca786", rawDiff)

    val blobDiffInformationLoaded = ContentViewerModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(selectedFilePath, commitId)
      .blobDiffInformationLoaded(blobDiffInformation)
      .blobDiffLoaded(blobDiff)

    // when
    viewRenderer.render(blobDiffInformationLoaded)

    // then
    verify(view).setDeletionsInsertionsAndFilesChanged(deletions, insertions, changedFiles - 1)
    verify(view).commitMessageLabelVisible(true)
    verify(view).setCommitMessage(message)
    verify(view).displayContent(blobDiff)
    verifyNoMoreInteractions(view)
  }
}
