package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.NextMatchers.hasNoEffects
import com.spotify.mobius.test.NextMatchers.hasNoModel
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import io.redgreen.timelapse.changedfiles.ChangedFiles.ErrorRetrievingChangedFiles
import io.redgreen.timelapse.changedfiles.ChangedFiles.FilesChanged
import io.redgreen.timelapse.changedfiles.ChangedFiles.NoOtherFilesChanged
import io.redgreen.timelapse.changedfiles.ChangedFiles.Retrieving
import io.redgreen.timelapse.changedfiles.ChangedFilesModel.HasSelection
import io.redgreen.timelapse.changedfiles.ChangedFilesModel.NoSelection
import io.redgreen.timelapse.mobius.spec
import org.junit.jupiter.api.Test

class ChangedFilesUpdateTest {
  private val withUpdateSpec = ChangedFilesUpdate.spec()
  private val commitId = "commit-id"
  private val selectedFilePath = "app/build.gradle"

  @Test
  fun `when user selects a file's revision it should begin retrieving other files that changed in the commit`() {
    val noSelectionState = NoSelection

    withUpdateSpec
      .given(noSelectionState)
      .whenEvent(RevisionSelected(commitId, selectedFilePath))
      .then(
        assertThatNext(
          hasModel(HasSelection(commitId, selectedFilePath, Retrieving)),
          hasEffects(FetchChangedFiles(commitId, selectedFilePath))
        )
      )
  }

  @Test
  fun `when there are no other changed files, it should show no other files changed`() {
    val revisionSelectedState = NoSelection
      .revisionSelected(commitId, selectedFilePath)
    val noChangedFiles = emptyList<String>()

    withUpdateSpec
      .given(revisionSelectedState)
      .whenEvent(io.redgreen.timelapse.changedfiles.NoOtherFilesChanged)
      .then(
        assertThatNext(
          hasModel(HasSelection(commitId, selectedFilePath, NoOtherFilesChanged)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when there are other changed files, it should display those changed files`() {
    val revisionSelectedState = NoSelection
      .revisionSelected(commitId, selectedFilePath)
    val changedFiles = listOf("build.gradle", "settings.gradle")

    withUpdateSpec
      .given(revisionSelectedState)
      .whenEvent(SomeFilesChanged(changedFiles))
      .then(
        assertThatNext(
          hasModel(HasSelection(commitId, selectedFilePath, FilesChanged(changedFiles))),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when the application is unable to get the changed files, it should display an error`() {
    val revisionSelectedState = NoSelection
      .revisionSelected(commitId, selectedFilePath)

    withUpdateSpec
      .given(revisionSelectedState)
      .whenEvent(UnableToRetrieveChangedFiles)
      .then(
        assertThatNext(
          hasModel(HasSelection(commitId, selectedFilePath, ErrorRetrievingChangedFiles)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when user see's an error fetching changed files, she should be able to retry`() {
    val errorRetrievingChangedFilesState = (NoSelection
      .revisionSelected(commitId, selectedFilePath) as HasSelection)
      .unableToRetrieveChangedFiles()

    withUpdateSpec
      .given(errorRetrievingChangedFilesState)
      .whenEvent(RetryRetrievingChangedFiles)
      .then(
        assertThatNext(
          hasModel(HasSelection(commitId, selectedFilePath, Retrieving)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when user selects a file from the list of changed files, it should display the diff for the file`() {
    val someFilesChangedState = (NoSelection
      .revisionSelected(commitId, selectedFilePath) as HasSelection)
      .someFilesChanged(listOf("README.md", "settings.gradle"))

    withUpdateSpec
      .given(someFilesChangedState)
      .whenEvent(SelectChangedFile(1))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(ChangedFileSelected(commitId, "settings.gradle"))
        )
      )
  }
}
