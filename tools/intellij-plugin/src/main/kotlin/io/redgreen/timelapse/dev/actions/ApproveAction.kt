package io.redgreen.timelapse.dev.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys

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

    existingApprovedVirtualFile?.delete(this)
    receivedVirtualFile.rename(this, approvedFileName)
  }
}
