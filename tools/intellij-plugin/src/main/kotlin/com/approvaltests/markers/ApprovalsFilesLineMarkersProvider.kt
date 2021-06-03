package com.approvaltests.markers

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.RIGHT
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import kotlin.random.Random

class ApprovalsFilesLineMarkersProvider : LineMarkerProvider {
  private val revealFeature = true

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

    val expression = PsiTreeUtil.findChildOfType(element, KtDotQualifiedExpression::class.java)
    return if (expression != null && isApprovalsVerifyCall(expression)) {
      val randomIconName = allIcons[Random.nextInt(allIcons.size)]
      val icon = IconLoader.getIcon("icons/$randomIconName.svg", this::class.java)

      val funKeyword = getFunKeyword(element)
      val textRange = funKeyword.textRange
      LineMarkerInfo(element, textRange, icon, null, null, RIGHT) { "Approvals test" }
    } else {
      null
    }
  }
}
