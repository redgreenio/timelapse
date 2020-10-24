package io.redgreen.timelapse.changedfiles

import com.google.common.truth.Truth.assertThat
import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.NextMatchers.hasNoEffects
import com.spotify.mobius.test.NextMatchers.hasNoModel
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import io.redgreen.timelapse.changedfiles.GetChangedFiles.GetChangedFilesFailure
import io.redgreen.timelapse.changedfiles.GetChangedFiles.GetChangedFilesFailure.Unknown
import io.redgreen.timelapse.mobius.AsyncOp.Companion.failure
import io.redgreen.timelapse.mobius.spec
import io.redgreen.timelapse.vcs.ChangedFile
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import org.junit.jupiter.api.Test

class ChangedFilesUpdateTest {
  private val withUpdateSpec = ChangedFilesUpdate.spec()
  private val commitId = "commit-id"
  private val filePath = "app/build.gradle"

  @Test
  fun `when user selects a file path and revision it should begin loading other files that changed in the commit`() {
    val noFileAndRevisionSelectedModel = ChangedFilesModel
      .noFileAndRevisionSelected()

    withUpdateSpec
      .given(noFileAndRevisionSelectedModel)
      .whenEvent(FileAndRevisionSelected(filePath, commitId))
      .then(
        assertThatNext(
          hasModel(noFileAndRevisionSelectedModel.fileAndRevisionSelected(filePath, commitId)),
          hasEffects(GetChangedFiles(commitId, filePath))
        )
      )
  }

  @Test
  fun `when commit does not have any other changed files, it should show no other files changed`() {
    val fileAndRevisionSelectedModel = ChangedFilesModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(filePath, commitId)

    withUpdateSpec
      .given(fileAndRevisionSelectedModel)
      .whenEvent(NoOtherFilesChanged)
      .then(
        assertThatNext(
          hasModel(fileAndRevisionSelectedModel.noOtherFilesChanged()),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when the commit has other files that were modified, it should display those modified files`() {
    val fileAndRevisionSelectedModel = ChangedFilesModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(filePath, commitId)
    val changedFiles = listOf("build.gradle", "settings.gradle").map(::Modification)

    withUpdateSpec
      .given(fileAndRevisionSelectedModel)
      .whenEvent(SomeMoreFilesChanged(changedFiles))
      .then(
        assertThatNext(
          hasModel(fileAndRevisionSelectedModel.someMoreFilesChanged(changedFiles)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when the application is unable to get the changed files, it should display an error`() {
    val fileAndRevisionSelectedModel = ChangedFilesModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(filePath, commitId)

    val gettingChangedFilesFailedModel = fileAndRevisionSelectedModel
      .gettingChangedFilesFailed()

    withUpdateSpec
      .given(fileAndRevisionSelectedModel)
      .whenEvent(GettingChangedFilesFailed)
      .then(
        assertThatNext(
          hasModel(gettingChangedFilesFailedModel),
          hasNoEffects()
        )
      )
    assertThat(gettingChangedFilesFailedModel.getChangedFilesAsyncOp)
      .isEqualTo(failure<List<ChangedFile>, GetChangedFilesFailure>(Unknown))
  }

  @Test
  fun `when user see's an error fetching changed files, she should be able to retry`() {
    val errorRetrievingChangedFilesModel = ChangedFilesModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(filePath, commitId)
      .gettingChangedFilesFailed()

    withUpdateSpec
      .given(errorRetrievingChangedFilesModel)
      .whenEvent(RetryGettingChangedFiles)
      .then(
        assertThatNext(
          hasModel(errorRetrievingChangedFilesModel.retryGettingChangedFiles()),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when user selects a file from the list of changed files, it should display the diff for the file`() {
    val someFilesChangedState = ChangedFilesModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(filePath, commitId)
      .someMoreFilesChanged(listOf("README.md", "settings.gradle").map(::Modification))

    withUpdateSpec
      .given(someFilesChangedState)
      .whenEvent(ChangedFileSelected(1))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(ShowDiff(commitId, Modification("settings.gradle")))
        )
      )
  }
}
