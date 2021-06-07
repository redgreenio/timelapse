package com.approvaltests.actions

import com.approvaltests.intellij.approve
import com.approvaltests.model.ApprovalFile
import com.approvaltests.model.ApprovalFile.Approved
import com.approvaltests.model.ApprovalFile.Received
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.IconLoader

class ApproveAction : AnAction() {
  companion object {
    private val approveIcon = IconLoader.getIcon("icons/approve.svg", ApproveAction::class.java)
  }

  override fun update(e: AnActionEvent) {
    val projectPresent = e.project != null
    val virtualFile = e.dataContext.getData(PlatformDataKeys.VIRTUAL_FILE)
    val selectedReceivedFile = virtualFile != null && ApprovalFile.from(virtualFile) is Received

    with(e.presentation) {
      icon = approveIcon
      isEnabledAndVisible = projectPresent && selectedReceivedFile
    }
  }

  override fun actionPerformed(e: AnActionEvent) {
    val receivedVirtualFile = e.dataContext.getData(PlatformDataKeys.VIRTUAL_FILE)
    receivedVirtualFile ?: return

    val received = Received(receivedVirtualFile)
    val approved = received.counterpart() as? Approved

    WriteCommandAction.runWriteCommandAction(e.project) { approve(this, received, approved) }
  }
}
