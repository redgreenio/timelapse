package com.approvaltests.markers.actions

import com.approvaltests.actions.ApproveAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.IconLoader

class ApproveReceivedFile(private val isRelevant: Boolean) : AnAction() {
  companion object {
    private val approveIcon = IconLoader.getIcon("icons/approve.svg", ApproveAction::class.java)
  }

  override fun update(e: AnActionEvent) {
    with(e.presentation) {
      icon = approveIcon
      text = "Approve Received File"
      isEnabled = isRelevant
    }
  }

  override fun actionPerformed(e: AnActionEvent) {
    TODO("Not yet implemented")
  }
}
