package com.approvaltests.settings

import com.approvaltests.settings.ApprovalTestsSettings.ViewFileIn.RIGHT_SPLIT
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
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
    },

    POP_UP_WINDOW {
      override val displayName: String = "Pop-up window"
    },

    NEW_WINDOW {
      override val displayName: String = "New window"
    };

    companion object {
      fun fromDisplayName(displayName: String): ViewFileIn {
        return values().first { it.displayName == displayName }
      }
    }

    abstract val displayName: String
  }
}
