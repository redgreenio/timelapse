package io.redgreen.timelapse.dev.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile

class ApproveAction : AnAction() {
  companion object {
    const val APPROVED_SLUG = ".approved."
    const val RECEIVED_SLUG = ".received."
  }

  override fun update(e: AnActionEvent) {
    val projectPresent = e.project != null
    val virtualFile = e.dataContext.getData(PlatformDataKeys.VIRTUAL_FILE)
    val selectedReceivedFile = virtualFile != null && virtualFile.name.contains(RECEIVED_SLUG)

    e.presentation.isVisible = projectPresent && selectedReceivedFile
  }

  override fun actionPerformed(e: AnActionEvent) {
    val receivedVirtualFile = e.dataContext.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return

    val receivedFileName = receivedVirtualFile.name
    val approvedFileName = receivedFileName.replace(RECEIVED_SLUG, APPROVED_SLUG)
    val existingApprovedVirtualFile = receivedVirtualFile.parent.findChild(approvedFileName)

    val approve = { approve(existingApprovedVirtualFile, approvedFileName, receivedVirtualFile) }
    WriteCommandAction.runWriteCommandAction(e.project, approve)
  }

  private fun approve(
    approvedVirtualFile: VirtualFile?,
    approvedFileName: String,
    receivedVirtualFile: VirtualFile
  ) {
    if (approvedVirtualFile == null) {
      receivedVirtualFile.rename(this, approvedFileName)
    } else {
      val fileDocumentManager = FileDocumentManager.getInstance()
      val document = fileDocumentManager.getDocument(approvedVirtualFile)
      if (document != null && document.isWritable) {
        document.setText(receivedVirtualFile.readText())
        receivedVirtualFile.delete(this)
      }
    }
  }
}
