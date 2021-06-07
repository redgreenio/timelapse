package com.approvaltests.markers

import com.approvaltests.markers.actions.ApproveReceivedFile
import com.approvaltests.markers.actions.CompareReceivedWithApproved
import com.approvaltests.markers.actions.ViewApprovedFile
import com.approvaltests.markers.actions.ViewReceivedFile
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import kotlin.random.Random

class ApprovalsFilesLineMarkersProvider : LineMarkerProvider {
  private val revealFeature = false

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    if (!revealFeature || element !is KtNamedFunction) {
      return null
    }

    val expressions = PsiTreeUtil.findChildrenOfType(element, KtDotQualifiedExpression::class.java)
    return if (!hasApprovalsVerifyCall(expressions)) {
      null
    } else {
      val allIcons = GutterIcon.values()
      val selectedIcon = allIcons[Random.nextInt(allIcons.size)]
      val randomIconName = selectedIcon.iconResourceName
      val icon = IconLoader.getIcon("icons/$randomIconName.svg", this::class.java)
      ApprovalTestLinerMarkerInfo(element, icon, getApprovalTestActionGroup(selectedIcon.enabledActions))
    }
  }

  private fun getApprovalTestActionGroup(
    enabledActions: Set<Class<out AnAction>>
  ): DefaultActionGroup {
    val allActions = listOf(
      CompareReceivedWithApproved(CompareReceivedWithApproved::class.java in enabledActions),
      ViewReceivedFile(ViewReceivedFile::class.java in enabledActions),
      ViewApprovedFile(ViewApprovedFile::class.java in enabledActions),
      ApproveReceivedFile(ApproveReceivedFile::class.java in enabledActions)
    )

    return DefaultActionGroup(allActions)
  }
}
