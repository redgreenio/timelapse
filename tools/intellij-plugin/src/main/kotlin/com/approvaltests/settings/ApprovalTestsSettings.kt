package com.approvaltests.settings

import com.approvaltests.settings.ApprovalTestsSettings.ViewFileIn.RIGHT_SPLIT
import com.intellij.ide.actions.OpenInRightSplitAction
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "ApprovalTestsSettings", storages = [Storage(value = "approval-tests.xml")])
data class ApprovalTestsSettings(
  var viewFileIn: ViewFileIn = RIGHT_SPLIT
) : PersistentStateComponent<ApprovalTestsSettings> {
  companion object {
    fun getInstance(project: Project): ApprovalTestsSettings {
      return ServiceManager.getService(project, ApprovalTestsSettings::class.java)
    }
  }

  override fun getState(): ApprovalTestsSettings {
    return this
  }

  override fun loadState(settings: ApprovalTestsSettings) {
    XmlSerializerUtil.copyBean(settings, this)
  }

  enum class ViewFileIn {
    RIGHT_SPLIT {
      override val displayName: String = "Right split"

      override fun open(project: Project, virtualFile: VirtualFile) {
        OpenInRightSplitAction.openInRightSplit(project, virtualFile, null, true)
      }
    },

    POP_UP_WINDOW {
      override val displayName: String = "Pop-up window"

      override fun open(project: Project, virtualFile: VirtualFile) {
        (FileEditorManager.getInstance(project) as FileEditorManagerImpl).openFileInNewWindow(virtualFile)
      }
    },

    NEW_WINDOW {
      override val displayName: String = "New window"

      override fun open(project: Project, virtualFile: VirtualFile) {
        FileEditorManager.getInstance(project).openFile(virtualFile, true)
      }
    };

    companion object {
      fun fromDisplayName(displayName: String): ViewFileIn {
        return values().first { it.displayName == displayName }
      }
    }

    abstract val displayName: String
    abstract fun open(project: Project, virtualFile: VirtualFile)
  }
}
