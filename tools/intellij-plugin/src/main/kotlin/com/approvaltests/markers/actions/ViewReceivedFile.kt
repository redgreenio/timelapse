package com.approvaltests.markers.actions

import com.approvaltests.model.FunctionCoordinates
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.util.IconLoader

class ViewReceivedFile(
  private val coordinates: FunctionCoordinates,
  private val isRelevant: Boolean
) : AnAction() {
  private val actionIcon = IconLoader.getIcon("icons/view-received.svg", this::class.java)

  override fun update(e: AnActionEvent) {
    with(e.presentation) {
      icon = actionIcon
      text = "View Received File"
      isEnabled = isRelevant
    }
  }

  override fun actionPerformed(e: AnActionEvent) {
    val testFileDirectory = e.getData(PlatformDataKeys.VIRTUAL_FILE)!!.parent
    val receivedFile = testFileDirectory.children
      .find { it.name.startsWith(coordinates.bestGuessReceivedFileNamePrefix()) }

    receivedFile?.let {
      FileEditorManager.getInstance(e.project!!).openFile(it, true)
    }
  }
}
