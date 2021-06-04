package com.approvaltests.actions

import com.approvaltests.model.ApprovalFile
import com.approvaltests.model.ApprovalFile.Approved
import com.approvaltests.model.ApprovalFile.Received
import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

class CompareWithAction : AnAction() {
  companion object {
    private const val TEXT_COMPARE_WITH_APPROVED = "Compare with Approved File"
    private const val TEXT_COMPARE_WITH_RECEIVED = "Compare with Received File"

    private val compareWithReceivedIcon = IconLoader.getIcon("icons/compareWithReceived.svg", ApproveAction::class.java)
    private val compareWithApprovedIcon = IconLoader.getIcon("icons/compareWithApproved.svg", ApproveAction::class.java)
  }

  sealed class PresentationAssets(
    private val icon: Icon,
    private val text: String,
    private val description: String
  ) {
    companion object {
      fun from(approvalFile: ApprovalFile): PresentationAssets {
        val counterpart = approvalFile.counterpart()!!
        val counterpartFileName = counterpart.virtualFile.name
        val description = "Compare with '$counterpartFileName'"

        return if (counterpart is Received) {
          ReceivedAssets(compareWithReceivedIcon, TEXT_COMPARE_WITH_RECEIVED, description)
        } else {
          ApprovedAssets(compareWithApprovedIcon, TEXT_COMPARE_WITH_APPROVED, description)
        }
      }
    }

    fun bind(presentation: Presentation) {
      with(presentation) {
        icon = this@PresentationAssets.icon
        text = this@PresentationAssets.text
        description = this@PresentationAssets.description
      }
    }

    class ApprovedAssets(icon: Icon, text: String, description: String) : PresentationAssets(icon, text, description)
    class ReceivedAssets(icon: Icon, text: String, description: String) : PresentationAssets(icon, text, description)
  }

  override fun update(e: AnActionEvent) {
    val isActionRelevant = isActionRelevantToContext(e.project, e.dataContext)
    val presentation = e.presentation

    if (isActionRelevant) {
      val approvalFile = ApprovalFile.from(getVirtualFile(e.dataContext)!!)!!
      PresentationAssets.from(approvalFile).bind(e.presentation)
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
    val diffContentFactory = DiffContentFactory.getInstance()
    val sideA = diffContentFactory.create(project, received.virtualFile)
    val sideB = diffContentFactory.create(project, approved.virtualFile)

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
