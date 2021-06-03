package com.approvaltests.markers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtNamedFunction

fun getKtNamedFunction(childElement: PsiElement): KtNamedFunction? {
  var parent = childElement.parent
  while (parent != null) {
    if (parent is KtNamedFunction) {
      return parent
    } else {
      parent = parent.parent
    }
  }
  return null
}

fun getFunKeyword(namedFunction: KtNamedFunction): PsiElement {
  return namedFunction.funKeyword!!
}
