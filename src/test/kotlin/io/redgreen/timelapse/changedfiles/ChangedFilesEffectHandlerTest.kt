package io.redgreen.timelapse.changedfiles

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Single
import io.redgreen.timelapse.changedfiles.contracts.ReadingAreaContract
import io.redgreen.timelapse.domain.VcsRepositoryService
import io.redgreen.timelapse.mobius.EffectHandlerTestCase
import org.junit.After
import org.junit.jupiter.api.Test

class ChangedFilesEffectHandlerTest {
  private val vcsRepositoryService = mock<VcsRepositoryService>()
  private val readingAreaContract = mock<ReadingAreaContract>()
  private val effectHandler = ChangedFilesEffectHandler.from(vcsRepositoryService, readingAreaContract)
  private val testCase = EffectHandlerTestCase(effectHandler)

  @After
  fun teardown() {
    verifyNoMoreInteractions(readingAreaContract)
  }

  @Test
  fun `it should retrieve files that changed along with the selected file from a vcs repository`() {
    // given
    val changedFilePaths = listOf("settings.gradle", "build.gradle")
    whenever(vcsRepositoryService.getChangedFilePaths("commit-id"))
      .thenReturn(Single.just(changedFilePaths))

    // when
    testCase.dispatch(GetChangedFiles("commit-id", "app/build.gradle"))

    // then
    testCase
      .assertOutgoingEvents(SomeMoreFilesChanged(changedFilePaths))
  }

  @Test
  fun `it should notify when no other files were changed in the commit`() {
    // given
    whenever(vcsRepositoryService.getChangedFilePaths("commit-id"))
      .thenReturn(Single.just(listOf("app/build.gradle")))

    // when
    testCase.dispatch(GetChangedFiles("commit-id", "app/build.gradle"))

    // then
    testCase
      .assertOutgoingEvents(NoOtherFilesChanged)
  }

  @Test
  fun `it should notify when there is an error while getting changed files from a vcs repository`() {
    // given
    whenever(vcsRepositoryService.getChangedFilePaths("commit-id"))
      .thenReturn(Single.error(RuntimeException("Uh oh! there goes my treat :(")))

    // when
    testCase.dispatch(GetChangedFiles("commit-id", "app/build.gradle"))

    // then
    testCase
      .assertOutgoingEvents(GettingChangedFilesFailed)
  }

  @Test
  fun `it should show diff if a file path is selected`() {
    // when
    testCase.dispatch(ShowDiff("commit-id", "app/build.gradle"))

    // then
    testCase.assertNoOutgoingEvents()
    verify(readingAreaContract).showDiff("commit-id", "app/build.gradle")
  }
}
