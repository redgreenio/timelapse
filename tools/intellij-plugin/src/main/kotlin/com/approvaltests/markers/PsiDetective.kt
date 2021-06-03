package com.approvaltests.markers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.intentions.calleeName
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.debugText.getDebugText

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

fun isApprovalsVerifyCall(expression: KtDotQualifiedExpression): Boolean {
  val receiverIsApprovals = expression.receiverExpression.getDebugText() == "Approvals"
  val callerIsVerify = expression.calleeName == "verify"
  return receiverIsApprovals && callerIsVerify
}
