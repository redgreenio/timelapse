package com.approvaltests.markers

import com.approvaltests.markers.actions.ApproveReceivedFile
import com.approvaltests.markers.actions.CompareReceivedWithApproved
import com.approvaltests.markers.actions.ViewApprovedFile
import com.approvaltests.markers.actions.ViewReceivedFile
import com.approvaltests.model.FunctionCoordinates
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilBase
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

class ApprovalsFilesLineMarkersProvider : LineMarkerProvider {
  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    if (element !is KtNamedFunction) {
      return null
    }

    val expressions = PsiTreeUtil.findChildrenOfType(element, KtDotQualifiedExpression::class.java)
    return if (!hasApprovalsVerifyCall(expressions)) {
      null
    } else {
      val psiFile = PsiUtilBase.getRoot(element.node).psi as PsiFile
      val coordinates = getFunctionCoordinates(element)
      val selectedIcon = ApprovalGutterIconFactory.iconFrom(psiFile.virtualFile, coordinates)
      val icon = IconLoader.getIcon("icons/${selectedIcon.iconResourceName}.svg", this::class.java)
      ApprovalTestLinerMarkerInfo(element, icon, getApprovalTestActionGroup(coordinates, selectedIcon.enabledActions))
    }
  }

  private fun getApprovalTestActionGroup(
    coordinates: FunctionCoordinates,
    enabledActions: Set<Class<out AnAction>>
  ): DefaultActionGroup {
    val allActions = listOf(
      CompareReceivedWithApproved(coordinates, CompareReceivedWithApproved::class.java in enabledActions),
      ViewReceivedFile(coordinates, ViewReceivedFile::class.java in enabledActions),
      ViewApprovedFile(coordinates, ViewApprovedFile::class.java in enabledActions),
      ApproveReceivedFile(coordinates, ApproveReceivedFile::class.java in enabledActions)
    )

    return DefaultActionGroup(allActions)
  }
}
