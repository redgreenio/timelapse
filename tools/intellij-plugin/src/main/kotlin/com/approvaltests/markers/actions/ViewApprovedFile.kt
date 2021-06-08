package com.approvaltests.markers.actions

import com.approvaltests.model.FunctionCoordinates
import com.approvaltests.settings.ApprovalTestsSettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.util.IconLoader

class ViewApprovedFile(
  private val coordinates: FunctionCoordinates,
  private val isRelevant: Boolean
) : AnAction() {
  private val actionIcon = IconLoader.getIcon("icons/view-approved.svg", this::class.java)

  override fun update(e: AnActionEvent) {
    with(e.presentation) {
      icon = actionIcon
      text = "View Approved File"
      isEnabled = isRelevant
    }
  }

  override fun actionPerformed(e: AnActionEvent) {
    val testFileDirectory = e.getData(PlatformDataKeys.VIRTUAL_FILE)!!.parent
    val approvedFile = testFileDirectory.children
      .find { it.name.startsWith(coordinates.bestGuessApprovedFileNamePrefix()) }

    val project = e.project ?: return
    val viewInFile = ApprovalTestsSettings.getInstance(project).viewFileIn
    viewInFile.open(project, approvedFile!!)
  }
}
