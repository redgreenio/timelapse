package com.approvaltests.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.lang.Language
import com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.RIGHT
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement

class ApprovalsFilesLineMarkersProvider : LineMarkerProvider {
  private val revealFeature = false
  private val icon = IconLoader.getIcon("icons/nonExistent-nonEmpty.svg", this::class.java)

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    if (!revealFeature) {
      return null
    }

    val isKotlin = element.language.isKindOf(Language.findLanguageByID("kotlin"))
    return if (isKotlin && element.node.text == "fun") {
      LineMarkerInfo(element, element.textRange, icon, null, null, RIGHT) { "Approvals test" }
    } else {
      null
    }
  }
}
