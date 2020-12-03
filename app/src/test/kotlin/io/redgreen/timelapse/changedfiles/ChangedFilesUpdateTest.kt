package io.redgreen.timelapse.changedfiles

import com.google.common.truth.Truth.assertThat
import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.NextMatchers.hasNoEffects
import com.spotify.mobius.test.NextMatchers.hasNoModel
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import io.redgreen.timelapse.mobius.AsyncOp.Companion.failure
import io.redgreen.timelapse.mobius.spec
import io.redgreen.timelapse.vcs.ChangedFile
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import org.junit.jupiter.api.Test

class ChangedFilesUpdateTest {
  private val withUpdateSpec = ChangedFilesUpdate.spec()
  private val commitId = "commit-id"
  private val selectedFilePath = "app/build.gradle"

  @Test
  fun `when user selects a file path and revision it should begin loading other files that changed in the commit`() {
    val noFileAndRevisionSelectedModel = ChangedFilesModel
      .noFileAndRevisionSelected()

    withUpdateSpec
      .given(noFileAndRevisionSelectedModel)
      .whenEvent(FileAndRevisionSelected(selectedFilePath, commitId))
      .then(
        assertThatNext(
          hasModel(noFileAndRevisionSelectedModel.fileAndRevisionSelected(selectedFilePath, commitId)),
          hasEffects(GetChangedFiles(commitId, selectedFilePath))
        )
      )
  }

  @Test
  fun `when commit does not have any other changed files, it should show no other files changed`() {
    val fileAndRevisionSelectedModel = ChangedFilesModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(selectedFilePath, commitId)

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
      .fileAndRevisionSelected(selectedFilePath, commitId)
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
      .fileAndRevisionSelected(selectedFilePath, commitId)

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
      .isEqualTo(failure<List<ChangedFile>, GetChangedFiles.Failure>(GetChangedFiles.Failure.Unknown))
  }

  @Test
  fun `when user see's an error fetching changed files, she should be able to retry`() {
    val errorRetrievingChangedFilesModel = ChangedFilesModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(selectedFilePath, commitId)
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
      .fileAndRevisionSelected(selectedFilePath, commitId)
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

  @Test
  fun `when the commit has  files that were modified, it should ignore the currently selected file from the list`() {
    val fileAndRevisionSelectedModel = ChangedFilesModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected(selectedFilePath, commitId)
    val changedFiles = listOf("build.gradle", "settings.gradle", selectedFilePath).map(::Modification)

    val expectedChangedFiles = listOf("build.gradle", "settings.gradle").map(::Modification)
    withUpdateSpec
      .given(fileAndRevisionSelectedModel)
      .whenEvent(SomeMoreFilesChanged(changedFiles))
      .then(
        assertThatNext(
          hasModel(fileAndRevisionSelectedModel.someMoreFilesChanged(expectedChangedFiles)),
          hasNoEffects()
        )
      )
  }
}
