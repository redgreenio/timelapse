package com.approvaltests.intellij

import com.approvaltests.model.ApprovalFile
import com.approvaltests.model.ApprovalFile.Approved
import com.approvaltests.model.ApprovalFile.Received
import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
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

fun approve(
  requestor: AnAction,
  received: Received,
  existingApproved: Approved?
) {
  if (existingApproved == null) {
    received.virtualFile.rename(requestor, received.approvedFileName)
  } else {
    val fileDocumentManager = FileDocumentManager.getInstance()
    val document = fileDocumentManager.getDocument(existingApproved.virtualFile)
    if (document != null && document.isWritable) {
      copyReceivedContentToApproved(fileDocumentManager, received, document)
      received.virtualFile.delete(requestor)
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
