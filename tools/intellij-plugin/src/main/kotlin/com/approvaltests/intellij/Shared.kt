package com.approvaltests.intellij

import com.approvaltests.model.ApprovalFile
import com.approvaltests.model.ApprovalFile.Approved
import com.approvaltests.model.ApprovalFile.Received
import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.project.Project

fun compare(
  project: Project,
  received: Received,
  approved: Approved
) {
  DiffManager.getInstance().showDiff(project, createDiffRequest(project, received, approved))
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
