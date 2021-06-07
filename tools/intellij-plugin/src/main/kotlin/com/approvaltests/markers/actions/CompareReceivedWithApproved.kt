package com.approvaltests.markers.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.IconLoader

class CompareReceivedWithApproved(private val isRelevant: Boolean) : AnAction() {
  companion object {
    private val compareIcon = IconLoader.getIcon("icons/compareReceivedApproved.svg", this::class.java)
  }

  override fun update(e: AnActionEvent) {
    with(e.presentation) {
      icon = compareIcon
      text = "Compare Received <> Approved"
      isEnabled = isRelevant
    }
  }

  override fun actionPerformed(e: AnActionEvent) {
    TODO("Not yet implemented")
  }
}
