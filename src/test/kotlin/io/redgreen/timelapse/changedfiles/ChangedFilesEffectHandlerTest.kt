package io.redgreen.timelapse.changedfiles

import io.reactivex.rxjava3.core.Single
import io.redgreen.timelapse.domain.FakeVcsRepositoryService
import io.redgreen.timelapse.mobius.EffectHandlerTestCase
import org.junit.jupiter.api.Test

class ChangedFilesEffectHandlerTest {
  @Test
  fun `it should retrieve files that changed along with the selected file from a vcs repository`() {
    // given
    val changedFilePaths = listOf("settings.gradle", "build.gradle")
    val vcsRepositoryService = object : FakeVcsRepositoryService() {
      override fun getChangedFilePaths(commitId: String): Single<ChangedFiles> =
        Single.just(changedFilePaths)
    }

    val effectHandler = ChangedFilesEffectHandler.from(vcsRepositoryService)
    val testCase = EffectHandlerTestCase(effectHandler)

    // when
    testCase.dispatch(GetChangedFiles("commit-id", "app/build.gradle"))

    // then
    testCase
      .assertOutgoingEvents(SomeMoreFilesChanged(changedFilePaths))
  }

  @Test
  fun `it should notify when no other files were changed in the commit`() {
    // given
    val vcsRepositoryService = object : FakeVcsRepositoryService() {
      override fun getChangedFilePaths(commitId: String): Single<ChangedFiles> =
        Single.just(listOf("app/build.gradle"))
    }

    val effectHandler = ChangedFilesEffectHandler.from(vcsRepositoryService)
    val testCase = EffectHandlerTestCase(effectHandler)

    // when
    testCase.dispatch(GetChangedFiles("commit-id", "app/build.gradle"))

    // then
    testCase
      .assertOutgoingEvents(NoOtherFilesChanged)
  }

  @Test
  fun `it should notify when there is an error while getting changed files from a vcs repository`() {
    // given
    val vcsRepositoryService = object : FakeVcsRepositoryService() {
      override fun getChangedFilePaths(commitId: String): Single<ChangedFiles> =
        Single.error(RuntimeException("Uh oh! there goes my treat :("))
    }

    val effectHandler = ChangedFilesEffectHandler.from(vcsRepositoryService)
    val testCase = EffectHandlerTestCase(effectHandler)

    // when
    testCase.dispatch(GetChangedFiles("commit-id", "app/build.gradle"))

    // then
    testCase
      .assertOutgoingEvents(GettingChangedFilesFailed)
  }
}
