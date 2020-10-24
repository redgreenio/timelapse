package io.redgreen.timelapse.changedfiles

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Single
import io.redgreen.timelapse.changedfiles.contracts.ReadingAreaContract
import io.redgreen.timelapse.domain.VcsRepositoryService
import io.redgreen.timelapse.mobius.EffectHandlerTestCase
import io.redgreen.timelapse.vcs.FileChange.Addition
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
    val fileChanges = listOf("settings.gradle", "build.gradle").map(::Addition)
    whenever(vcsRepositoryService.getFileChanges("commit-id"))
      .thenReturn(Single.just(fileChanges))

    // when
    testCase.dispatch(GetChangedFiles("commit-id", "app/build.gradle"))

    // then
    testCase
      .assertOutgoingEvents(SomeMoreFilesChanged(fileChanges = fileChanges))
  }

  @Test
  fun `it should notify when no other files were changed in the commit`() {
    // given
    val fileChanges = listOf("app/build.gradle").map(::Addition)
    whenever(vcsRepositoryService.getFileChanges("commit-id"))
      .thenReturn(Single.just(fileChanges))

    // when
    testCase.dispatch(GetChangedFiles("commit-id", "app/build.gradle"))

    // then
    testCase
      .assertOutgoingEvents(NoOtherFilesChanged)
  }

  @Test
  fun `it should notify when there is an error while getting changed files from a vcs repository`() {
    // given
    whenever(vcsRepositoryService.getFileChanges("commit-id"))
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
