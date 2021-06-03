package com.approvaltests.markers

import com.approvaltests.markers.actions.ApproveReceivedFile
import com.approvaltests.markers.actions.CompareReceivedWithApproved
import com.approvaltests.markers.actions.ViewApprovedFileAction
import com.approvaltests.markers.actions.ViewReceivedFileAction
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import kotlin.random.Random

class ApprovalsFilesLineMarkersProvider : LineMarkerProvider {
  private val revealFeature = false

  private val allIcons = listOf(
    "nonEmpty-empty",
    "nonEmpty-nonEmpty",
    "nonEmpty-nonExistent",
    "nonExistent-nonEmpty",
    "nonExistent-nonExistent"
  )

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    if (!revealFeature || element !is KtNamedFunction) {
      return null
    }

    val expressions = PsiTreeUtil.findChildrenOfType(element, KtDotQualifiedExpression::class.java)
    return if (!hasApprovalsVerifyCall(expressions)) {
      null
    } else {
      val randomIconName = allIcons[Random.nextInt(allIcons.size)]
      val icon = IconLoader.getIcon("icons/$randomIconName.svg", this::class.java)
      ApprovalTestLinerMarkerInfo(element, icon, getApprovalTestActionGroup())
    }
  }

  private fun getApprovalTestActionGroup(): DefaultActionGroup {
    return DefaultActionGroup().apply {
      addAction(CompareReceivedWithApproved())
      addAction(ViewReceivedFileAction())
      addAction(ViewApprovedFileAction())
      addAction(ApproveReceivedFile())
    }
  }
}
