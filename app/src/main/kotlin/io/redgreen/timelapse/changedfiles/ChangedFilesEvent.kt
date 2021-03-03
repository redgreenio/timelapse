package io.redgreen.timelapse.changedfiles

import io.redgreen.architecture.mobius.annotations.EffectEvent
import io.redgreen.architecture.mobius.annotations.UiEvent
import io.redgreen.timelapse.vcs.ChangedFile

sealed class ChangedFilesEvent

@UiEvent
data class FileAndRevisionSelected(
  val selectedFilePath: String,
  val commitId: String
) : ChangedFilesEvent()

@UiEvent
object RetryGettingChangedFiles : ChangedFilesEvent() // TODO Implement UI before dispatching this event!

@UiEvent
data class ChangedFileSelected(val index: Int) : ChangedFilesEvent()

@EffectEvent(GetChangedFiles::class)
object NoOtherFilesChanged : ChangedFilesEvent()

@EffectEvent(GetChangedFiles::class)
data class SomeMoreFilesChanged(
  val changedFiles: List<ChangedFile>
) : ChangedFilesEvent()

@EffectEvent(GetChangedFiles::class)
object GettingChangedFilesFailed : ChangedFilesEvent()
