package io.redgreen.timelapse.changedfiles.view

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.redgreen.timelapse.changedfiles.ChangedFilesModel
import io.redgreen.timelapse.changedfiles.view.ChangedFilesViewMessage.NO_FILE_AND_REVISION_SELECTED
import io.redgreen.timelapse.changedfiles.view.ChangedFilesViewMessage.NO_OTHER_FILES_CHANGED
import io.redgreen.timelapse.changedfiles.view.ChangedFilesViewMessage.RETRY_GETTING_CHANGED_FILES
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import org.junit.jupiter.api.Test

class ChangedFilesViewRendererTest {
  private val view = mock<ChangedFilesView>()
  private val viewRenderer = ChangedFilesViewRenderer(view)

  @Test
  fun `it should render nothing selected`() {
    // given
    val noFileAndRevisionSelectedModel = ChangedFilesModel
      .noFileAndRevisionSelected()

    // when
    viewRenderer.render(noFileAndRevisionSelectedModel)

    // then
    verify(view).showMessage(NO_FILE_AND_REVISION_SELECTED)
    verify(view).setLoadingVisibility(false)
    verify(view).setChangedFilesListVisibility(false)
  }

  @Test
  fun `it should render loading changed files`() {
    // given
    val noOtherFilesChangedModel = ChangedFilesModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected("build.gradle", "commit-id")

    // when
    viewRenderer.render(noOtherFilesChangedModel)

    // then
    verify(view).setLoadingVisibility(true)
    verify(view).setChangedFilesListVisibility(false)
    verify(view).hideMessage()
  }

  @Test
  fun `it should render no other files changed`() {
    // given
    val noOtherFilesChangedModel = ChangedFilesModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected("build.gradle", "commit-id")
      .noOtherFilesChanged()

    // when
    viewRenderer.render(noOtherFilesChangedModel)

    // then
    verify(view).showMessage(NO_OTHER_FILES_CHANGED)
    verify(view).setLoadingVisibility(false)
    verify(view).setChangedFilesListVisibility(false)
  }

  @Test
  fun `it should render some more files changed`() {
    // given
    val fileChanges = listOf("settings.gradle", "build.gradle").map(::Addition)
    val someMoreFilesChangedModel = ChangedFilesModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected("app/build.gradle", "commit-id")
      .someMoreFilesChanged(fileChanges)

    // when
    viewRenderer.render(someMoreFilesChangedModel)

    // then
    verify(view).showChangedFiles(fileChanges)
    verify(view).hideMessage()
    verify(view).setLoadingVisibility(false)
  }

  @Test
  fun `it should render retry`() {
    // given
    val gettingChangedFilesFailedModel = ChangedFilesModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected("build.gradle", "commit-id")
      .gettingChangedFilesFailed()

    // when
    viewRenderer.render(gettingChangedFilesFailedModel)

    // then
    verify(view).showMessage(RETRY_GETTING_CHANGED_FILES)
    verify(view).setLoadingVisibility(false)
    verify(view).setChangedFilesListVisibility(false)
  }

  @Test
  fun `it should render retrying getting files changed`() {
    // given
    val retryGettingChangedFilesModel = ChangedFilesModel
      .noFileAndRevisionSelected()
      .fileAndRevisionSelected("build.gradle", "commit-id")
      .gettingChangedFilesFailed()
      .retryGettingChangedFiles()

    // when
    viewRenderer.render(retryGettingChangedFilesModel)

    // then
    verify(view).setLoadingVisibility(true)
    verify(view).setChangedFilesListVisibility(false)
    verify(view).hideMessage()
  }
}
