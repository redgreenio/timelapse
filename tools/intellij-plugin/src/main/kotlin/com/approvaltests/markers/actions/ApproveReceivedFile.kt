package com.approvaltests.markers.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ApproveReceivedFile : AnAction() {
  override fun update(e: AnActionEvent) {
    e.presentation.text = "Approve Received File"
  }

  override fun actionPerformed(e: AnActionEvent) {
    TODO("Not yet implemented")
  }
}
