package io.redgreen.timelapse.dev.templates

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiFile

private const val ID = "io.redgreen.timelapse.dev.templates"
private const val PRESENTABLE_NAME = "Timelapse Templates"

class TimelapseDevTemplateContextType : TemplateContextType(ID, PRESENTABLE_NAME) {
  override fun isInContext(file: PsiFile, offset: Int): Boolean =
    true
}
