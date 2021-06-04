package com.approvaltests.actions

import com.approvaltests.intellij.readText
import com.approvaltests.model.ApprovalFile
import com.approvaltests.model.ApprovalFile.Approved
import com.approvaltests.model.ApprovalFile.Received
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
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

    WriteCommandAction.runWriteCommandAction(e.project) { approve(received, approved) }
  }

  private fun approve(
    received: Received,
    existingApproved: Approved?
  ) {
    if (existingApproved == null) {
      received.virtualFile.rename(this, received.approvedFileName)
    } else {
      val fileDocumentManager = FileDocumentManager.getInstance()
      val document = fileDocumentManager.getDocument(existingApproved.virtualFile)
      if (document != null && document.isWritable) {
        copyReceivedContentToApproved(fileDocumentManager, received, document)
        deleteReceived(received)
      }
    }
  }

  private fun copyReceivedContentToApproved(
    fileDocumentManager: FileDocumentManager,
    received: Received,
    approvedDocument: Document
  ) {
    // Save latest changes in the 'received' file
    fileDocumentManager.getDocument(received.virtualFile)?.let { fileDocumentManager.saveDocument(it) }

    approvedDocument.setText(received.virtualFile.readText())
    fileDocumentManager.saveDocument(approvedDocument)
  }

  private fun deleteReceived(received: Received) {
    received.virtualFile.delete(this)
  }
}
