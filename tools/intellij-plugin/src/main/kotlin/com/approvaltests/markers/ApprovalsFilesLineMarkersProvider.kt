package com.approvaltests.markers

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.RIGHT
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtNamedFunction

class ApprovalsFilesLineMarkersProvider : LineMarkerProvider {
  private val revealFeature = true
  private val icon = IconLoader.getIcon("icons/nonExistent-nonEmpty.svg", this::class.java)

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    if (!revealFeature) {
      return null
    }

    return if (element is KtNamedFunction) {
      val funKeyword = getFunKeyword(element)
      val textRange = funKeyword.textRange
      LineMarkerInfo(element, textRange, icon, null, null, RIGHT) { "Approvals test" }
    } else {
      null
    }
  }
}
