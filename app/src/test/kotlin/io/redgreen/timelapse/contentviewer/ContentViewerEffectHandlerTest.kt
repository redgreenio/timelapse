package io.redgreen.timelapse.contentviewer

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Single
import io.redgreen.timelapse.domain.BlobDiff.Merge
import io.redgreen.timelapse.domain.BlobDiff.Simple
import io.redgreen.timelapse.mobius.EffectHandlerTestCase
import io.redgreen.timelapse.platform.ClipboardService
import io.redgreen.timelapse.platform.ImmediateSchedulersProvider
import io.redgreen.timelapse.vcs.VcsRepositoryService
import org.junit.jupiter.api.Test

class ContentViewerEffectHandlerTest {
  private val selectedFilePath = "selected/file/path"
  private val commitId = "commit-id"

  private val vcsRepositoryService = mock<VcsRepositoryService>()
  private val clipboardService = mock<ClipboardService>()

  private val effectHandler = ContentViewerEffectHandler.from(
    vcsRepositoryService,
    clipboardService,
    ImmediateSchedulersProvider
  )

  private val testCase = EffectHandlerTestCase(effectHandler)

  @Test
  fun `it should retrieve simple blob diff for selected file in a commit`() {
    // given
    whenever(vcsRepositoryService.getBlobDiff(selectedFilePath, commitId))
      .thenReturn(Single.just(getSimpleBlobDiff()))

    // when
    testCase.dispatch(LoadBlobDiff(selectedFilePath, commitId))

    // then
    testCase
      .assertOutgoingEvents(BlobDiffLoaded(getSimpleBlobDiff()))
  }

  @Test
  fun `it should retrieve a merge blob diff for selected file in a commit`() {
    // given
    whenever(vcsRepositoryService.getBlobDiff(selectedFilePath, commitId))
      .thenReturn(Single.just(getMergeBlobDiff()))

    // when
    testCase.dispatch(LoadBlobDiff(selectedFilePath, commitId))

    // then
    testCase
      .assertOutgoingEvents(BlobDiffLoaded(getMergeBlobDiff()))
  }

  @Test
  fun `it should return unable to load blob diff event in case of a failure`() {
    whenever(vcsRepositoryService.getBlobDiff(selectedFilePath, commitId))
      .thenReturn(Single.error(RuntimeException("Something went wrong :(")))

    // when
    testCase.dispatch(LoadBlobDiff(selectedFilePath, commitId))

    // then
    testCase
      .assertOutgoingEvents(UnableToLoadBlobDiff)
  }

  @Test
  fun `it should retrieve blob diff information for selected file in a commit`() {
    // given
    val blobDiffInformation = BlobDiffInformation(selectedFilePath, commitId, "some-commit-message", 1, 1, 1)
    whenever(vcsRepositoryService.getBlobDiffInformation(selectedFilePath, commitId))
      .thenReturn(Single.just(blobDiffInformation))

    // when
    testCase.dispatch(LoadBlobDiffInformation(selectedFilePath, commitId))

    // then
    testCase
      .assertOutgoingEvents(BlobDiffInformationLoaded(blobDiffInformation))
  }

  @Test
  fun `it should return unable to load blob diff information event in case of a failure`() {
    // given
    whenever(vcsRepositoryService.getBlobDiffInformation(selectedFilePath, commitId))
      .thenReturn(Single.error(RuntimeException("Uh oh! We didn't expect this to happen :(")))

    // when
    testCase.dispatch(LoadBlobDiffInformation(selectedFilePath, commitId))

    // then
    testCase
      .assertOutgoingEvents(UnableToLoadBlobDiffInformation)
  }

  @Test
  fun `it should copy commit id to clipboard`() {
    // when
    testCase.dispatch(CopyCommitIdToClipboard(commitId))

    // then
    verify(clipboardService).copy(commitId)
    verifyNoMoreInteractions(clipboardService)

    testCase
      .assertNoOutgoingEvents()
  }

  private fun getSimpleBlobDiff(): Simple {
    return Simple(
      "parent-commit-id",
      """
        diff --git a/file-1-copy.txt b/file-1-copy.txt
        new file mode 100644
        index 0000000..980a0d5
        --- /dev/null
        +++ b/file-1-copy.txt
        @@ -0,0 +1 @@
        +Hello World!
        
      """.trimIndent()
    )
  }

  private fun getMergeBlobDiff(): Merge {
    return Merge(
      listOf(
        Simple(
          "1865160d483f9b22dfa9b49d0305c167746d9f7a",
          """
            diff --git a/file-1.txt b/file-1.txt
            index 265d673..f17d600 100644
            --- a/file-1.txt
            +++ b/file-1.txt
            @@ -1 +1 @@
            -Hola, mundo!
            +Hola, world!
            
          """.trimIndent()
        ),

        Simple(
          "6ad80c13f9d08fdfc1bd0ab7299a2178183326a1",
          """
            diff --git a/file-1.txt b/file-1.txt
            index af5626b..f17d600 100644
            --- a/file-1.txt
            +++ b/file-1.txt
            @@ -1 +1 @@
            -Hello, world!
            +Hola, world!
            
          """.trimIndent()
        )
      )
    )
  }
}
