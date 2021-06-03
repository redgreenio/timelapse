package com.approvaltests.markers.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class CompareReceivedWithApproved : AnAction() {
  override fun update(e: AnActionEvent) {
    e.presentation.text = "Compare Received <> Approved"
  }

  override fun actionPerformed(e: AnActionEvent) {
    TODO("Not yet implemented")
  }
}
