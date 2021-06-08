package com.approvaltests.settings

import com.approvaltests.DISPLAY_NAME
import com.approvaltests.ID
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class ApprovalTestsConfigurable(val project: Project) : SearchableConfigurable {
  private lateinit var pluginSettingsForm: PluginSettingsForm

  override fun createComponent(): JComponent {
    pluginSettingsForm = PluginSettingsForm(ApprovalTestsSettings.getInstance(project))
    return pluginSettingsForm.root
  }

  override fun isModified(): Boolean {
    return pluginSettingsForm.isModified()
  }

  override fun apply() {
    pluginSettingsForm.applyChanges()
  }

  override fun reset() {
    pluginSettingsForm.resetChanges()
  }

  override fun getDisplayName(): String =
    DISPLAY_NAME

  override fun getId(): String =
    ID
}
