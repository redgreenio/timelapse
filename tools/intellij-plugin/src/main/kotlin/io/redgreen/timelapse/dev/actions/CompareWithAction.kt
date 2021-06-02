package io.redgreen.timelapse.dev.actions

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.redgreen.timelapse.dev.model.ApprovalFile
import io.redgreen.timelapse.dev.model.ApprovalFile.Approved
import io.redgreen.timelapse.dev.model.ApprovalFile.Received

class CompareWithAction : AnAction() {
  companion object {
    private const val TEXT_COMPARE_WITH_APPROVED = "Compare With Approved File"
    private const val TEXT_COMPARE_WITH_RECEIVED = "Compare With Received File"
  }

  override fun update(e: AnActionEvent) {
    val isActionRelevant = isActionRelevantToContext(e.project, e.dataContext)
    val presentation = e.presentation

    if (isActionRelevant) {
      presentation.text = getActionText(e.dataContext)
    }
    presentation.isEnabledAndVisible = isActionRelevant
  }

  override fun actionPerformed(e: AnActionEvent) {
    val virtualFile = getVirtualFile(e.dataContext) ?: return
    e.project ?: return

    val selectedFile = ApprovalFile.from(virtualFile)!!
    val (received, approved) = receivedAndApproved(selectedFile)
    DiffManager.getInstance().showDiff(e.project, createDiffRequest(e.project!!, received, approved))
  }

  private fun isActionRelevantToContext(project: Project?, dataContext: DataContext): Boolean {
    project ?: return false

    val virtualFile = getVirtualFile(dataContext)
    val approvalFile = virtualFile?.let { ApprovalFile.from(virtualFile) }
    val approvalFileCounterpart = approvalFile?.counterpart()
    return approvalFile != null && approvalFileCounterpart != null
  }

  private fun getActionText(dataContext: DataContext): String {
    val approvalFile = ApprovalFile.from(getVirtualFile(dataContext)!!)!!
    val counterpart = approvalFile.counterpart()!!

    return if (counterpart is Received) {
      TEXT_COMPARE_WITH_RECEIVED
    } else {
      TEXT_COMPARE_WITH_APPROVED
    }
  }

  private fun receivedAndApproved(selectedFile: ApprovalFile): Pair<Received, Approved> {
    return if (selectedFile is Approved) {
      selectedFile.counterpart()!! as Received to selectedFile
    } else {
      selectedFile as Received to selectedFile.counterpart()!! as Approved
    }
  }

  private fun createDiffRequest(
    project: Project,
    received: Received,
    approved: Approved
  ): SimpleDiffRequest {
    val sideA = DiffContentFactory.getInstance().create(project, received.virtualFile)
    val sideB = DiffContentFactory.getInstance().create(project, approved.virtualFile)
    return SimpleDiffRequest(title(received, approved), sideA, sideB, subtitle(received), subtitle(approved))
  }

  private fun title(received: Received, approved: Approved): String {
    return "${received.virtualFile.name} - ${approved.virtualFile.name} (${received.virtualFile.parent.path})"
  }

  private fun subtitle(approvalFile: ApprovalFile): String {
    return "${approvalFile.virtualFile.name} (${approvalFile.virtualFile.parent.path})"
  }

  private fun getVirtualFile(dataContext: DataContext): VirtualFile? =
    dataContext.getData(PlatformDataKeys.VIRTUAL_FILE)
}
