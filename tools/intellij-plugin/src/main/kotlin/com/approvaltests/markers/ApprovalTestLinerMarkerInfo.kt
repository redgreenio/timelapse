package com.approvaltests.markers

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.CENTER
import com.intellij.psi.PsiElement
import javax.swing.Icon

class ApprovalTestLinerMarkerInfo(
  element: PsiElement,
  icon: Icon,
  private val actionGroup: DefaultActionGroup
) : LineMarkerInfo<PsiElement>(element, element.textRange, icon, null, null, CENTER, { "" }) {
  override fun createGutterRenderer(): GutterIconRenderer {
    return object : LineMarkerGutterIconRenderer<PsiElement>(this) {
      override fun getClickAction(): AnAction? {
        return null
      }

      override fun isNavigateAction(): Boolean {
        return true
      }

      override fun getPopupMenuActions(): ActionGroup {
        return actionGroup
      }
    }
  }
}
