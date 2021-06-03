package com.approvaltests.actions

import com.approvaltests.intellij.readText
import com.approvaltests.model.ApprovalFile
import com.approvaltests.model.ApprovalFile.Approved
import com.approvaltests.model.ApprovalFile.Received
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileChooser.FileSystemTree
import com.intellij.openapi.fileEditor.FileDocumentManager
import java.io.IOException

class ApproveAction : AnAction() {
  companion object {
    private val LOGGER = Logger.getInstance(ApproveAction::class.java)
  }

  override fun update(e: AnActionEvent) {
    val projectPresent = e.project != null
    val virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE)
    val selectedReceivedFile = virtualFile != null && ApprovalFile.from(virtualFile) is Received

    e.presentation.isEnabledAndVisible = projectPresent && selectedReceivedFile
  }

  override fun actionPerformed(e: AnActionEvent) {
    val receivedVirtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE)
    receivedVirtualFile ?: return

    val received = Received(receivedVirtualFile)
    val approved = received.counterpart() as? Approved

    val fileSystemTree = e.getData(FileSystemTree.DATA_KEY)

    WriteCommandAction.runWriteCommandAction(e.project) { approve(received, approved, fileSystemTree) }
  }

  private fun approve(
    received: Received,
    existingApproved: Approved?,
    fileSystemTree: FileSystemTree?
  ) {
    if (existingApproved == null) {
      received.virtualFile.rename(this, received.approvedFileName)
    } else {
      val fileDocumentManager = FileDocumentManager.getInstance()
      val approvedFileDocument = fileDocumentManager.getDocument(existingApproved.virtualFile)

      if (approvedFileDocument != null && approvedFileDocument.isWritable) {
        copyReceivedContentToApproved(fileDocumentManager, received, approvedFileDocument)
        fileSystemTree?.select(existingApproved.virtualFile) { fileSystemTree.expand(received.virtualFile, null) }
        if (!deleteReceived(received)) {
          fileSystemTree?.select(received.virtualFile) { fileSystemTree.expand(received.virtualFile, null) }
        }
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

  private fun deleteReceived(received: Received): Boolean {
    return try {
      received.virtualFile.delete(this)
      true
    } catch (e: IOException) {
      LOGGER.trace(e)
      false
    }
  }
}
