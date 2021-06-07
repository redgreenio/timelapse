package com.approvaltests.markers.actions

import com.approvaltests.actions.ApproveAction
import com.approvaltests.intellij.approve
import com.approvaltests.model.ApprovalFile
import com.approvaltests.model.ApprovalFile.Approved
import com.approvaltests.model.ApprovalFile.Received
import com.approvaltests.model.FunctionCoordinates
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.IconLoader

class ApproveReceivedFile(
  private val coordinates: FunctionCoordinates,
  private val isRelevant: Boolean
) : AnAction() {
  companion object {
    private val approveIcon = IconLoader.getIcon("icons/approve.svg", ApproveAction::class.java)
  }

  override fun update(e: AnActionEvent) {
    with(e.presentation) {
      icon = approveIcon
      text = "Approve Received File"
      isEnabled = isRelevant
    }
  }

  override fun actionPerformed(e: AnActionEvent) {
    val testFileDirectory = e.getData(PlatformDataKeys.VIRTUAL_FILE)!!.parent
    val receivedVirtualFile = testFileDirectory.children
      .find { it.name.startsWith(coordinates.bestGuessReceivedFileNamePrefix()) }
    val received = ApprovalFile.from(receivedVirtualFile!!) as Received
    val existingApproved = received.counterpart() as? Approved

    WriteCommandAction.runWriteCommandAction(e.project) { approve(this, received, existingApproved) }
  }
}
