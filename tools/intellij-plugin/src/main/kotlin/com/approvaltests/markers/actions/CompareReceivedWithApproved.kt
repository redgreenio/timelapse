package com.approvaltests.markers.actions

import com.approvaltests.intellij.compare
import com.approvaltests.model.ApprovalFile
import com.approvaltests.model.ApprovalFile.Approved
import com.approvaltests.model.ApprovalFile.Received
import com.approvaltests.model.FunctionCoordinates
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.util.IconLoader

class CompareReceivedWithApproved(
  private val coordinates: FunctionCoordinates,
  private val isRelevant: Boolean
) : AnAction() {
  companion object {
    private val compareIcon = IconLoader.getIcon("icons/compareReceivedApproved.svg", this::class.java)
  }

  override fun update(e: AnActionEvent) {
    with(e.presentation) {
      icon = compareIcon
      text = "Compare Received <> Approved"
      isEnabled = isRelevant
    }
  }

  override fun actionPerformed(e: AnActionEvent) {
    val testFileDirectory = e.getData(PlatformDataKeys.VIRTUAL_FILE)!!.parent
    val approvedFile = testFileDirectory.children
      .find { it.name.startsWith(coordinates.bestGuessApprovedFileNamePrefix()) }

    val approved = ApprovalFile.from(approvedFile!!) as Approved
    val received = approved.counterpart() as Received

    compare(e.project!!, received, approved)
  }
}
