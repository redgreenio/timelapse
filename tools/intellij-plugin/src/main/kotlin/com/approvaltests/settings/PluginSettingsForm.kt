package com.approvaltests.settings

import com.approvaltests.settings.ApprovalTestsSettings.ViewFileIn
import java.awt.event.ActionEvent
import javax.swing.JComboBox
import javax.swing.JPanel

class PluginSettingsForm(
  private val initialSettings: ApprovalTestsSettings
) {
  internal lateinit var root: JPanel
  internal lateinit var viewFileInComboBox: JComboBox<*>

  private var currentSettings = ApprovalTestsSettings()

  private val updateActionListener = { _: ActionEvent ->
    updateSettingsFromUi()
    updateUiFromSettings(currentSettings)
  }

  init {
    currentSettings.loadState(initialSettings)
    updateUiFromSettings(currentSettings)
    viewFileInComboBox.addActionListener(updateActionListener)
  }

  fun applyChanges() {
    initialSettings.loadState(currentSettings)
  }

  fun resetChanges() {
    currentSettings = initialSettings.copy()
    updateUiFromSettings(currentSettings)
  }

  fun isModified(): Boolean =
    currentSettings != initialSettings

  private fun updateUiFromSettings(settings: ApprovalTestsSettings) {
    viewFileInComboBox.selectedItem = settings.viewFileIn.displayName
  }

  private fun updateSettingsFromUi() {
    currentSettings.viewFileIn = ViewFileIn.fromDisplayName(viewFileInComboBox.selectedItem!!.toString())
  }
}
