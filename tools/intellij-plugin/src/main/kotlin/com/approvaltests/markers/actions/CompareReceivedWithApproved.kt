package com.approvaltests.markers.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class CompareReceivedWithApproved : AnAction() {
  override fun update(e: AnActionEvent) {
    with(e.presentation) {
      icon = AllIcons.Actions.Diff
      text = "Compare Received <> Approved"
    }
  }

  override fun actionPerformed(e: AnActionEvent) {
    TODO("Not yet implemented")
  }
}
