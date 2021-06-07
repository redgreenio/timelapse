package com.approvaltests.markers

import com.approvaltests.markers.actions.ApproveReceivedFile
import com.approvaltests.markers.actions.CompareReceivedWithApproved
import com.approvaltests.markers.actions.ViewApprovedFile
import com.approvaltests.markers.actions.ViewReceivedFile
import com.intellij.openapi.actionSystem.AnAction

private val allActions = setOf(
  CompareReceivedWithApproved::class.java,
  ViewReceivedFile::class.java,
  ViewApprovedFile::class.java,
  ApproveReceivedFile::class.java
)

enum class GutterIcon {
  MISSING_MISSING {
    override val iconResourceName: String = "missing-missing"
    override val enabledActions: Set<Class<out AnAction>> = emptySet()
  },

  MISSING_PRESENT {
    override val iconResourceName: String = "missing-present"
    override val enabledActions: Set<Class<out AnAction>> = setOf(ViewApprovedFile::class.java)
  },

  PRESENT_MISSING {
    override val iconResourceName: String = "present-missing"
    override val enabledActions: Set<Class<out AnAction>> = setOf(
      ViewReceivedFile::class.java,
      ApproveReceivedFile::class.java
    )
  },

  PRESENT_EMPTY {
    override val iconResourceName: String = "present-empty"
    override val enabledActions: Set<Class<out AnAction>> = allActions
  },

  PRESENT_PRESENT_SAME {
    override val iconResourceName: String = "present-present-same"
    override val enabledActions: Set<Class<out AnAction>> = allActions
  },

  PRESENT_PRESENT_DIFFERENT {
    override val iconResourceName: String = "present-present-different"
    override val enabledActions: Set<Class<out AnAction>> = allActions
  };

  abstract val iconResourceName: String
  abstract val enabledActions: Set<Class<out AnAction>>
}
