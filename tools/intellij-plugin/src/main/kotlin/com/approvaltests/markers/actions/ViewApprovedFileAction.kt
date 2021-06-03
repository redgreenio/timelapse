package com.approvaltests.markers.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.IconLoader

class ViewApprovedFileAction : AnAction() {
  private val actionIcon = IconLoader.getIcon("icons/view-approved.svg", this::class.java)

  override fun update(e: AnActionEvent) {
    with(e.presentation) {
      icon = actionIcon
      text = "View Approved File"
    }
  }

  override fun actionPerformed(e: AnActionEvent) {
    TODO("Not yet implemented")
  }
}
