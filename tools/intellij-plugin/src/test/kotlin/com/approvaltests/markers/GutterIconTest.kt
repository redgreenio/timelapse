package com.approvaltests.markers

import com.approvaltests.markers.GutterIcon.MISSING_MISSING
import com.approvaltests.markers.GutterIcon.MISSING_PRESENT
import com.approvaltests.markers.GutterIcon.PRESENT_EMPTY
import com.approvaltests.markers.GutterIcon.PRESENT_MISSING
import com.approvaltests.markers.GutterIcon.PRESENT_PRESENT_DIFFERENT
import com.approvaltests.markers.GutterIcon.PRESENT_PRESENT_SAME
import com.approvaltests.markers.actions.ApproveReceivedFile
import com.approvaltests.markers.actions.CompareReceivedWithApproved
import com.approvaltests.markers.actions.ViewApprovedFile
import com.approvaltests.markers.actions.ViewReceivedFile
import com.google.common.truth.Truth.assertThat
import com.intellij.openapi.actionSystem.AnAction
import org.approvaltests.Approvals
import org.approvaltests.core.Options
import org.approvaltests.namer.NamerFactory
import org.approvaltests.writers.ApprovalTextWriter
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class GutterIconTest {
  private val svgOptions = Options().forFile().withExtension(".svg")

  companion object {
    @Suppress("unused") // Used by parameterized tests
    @JvmStatic
    fun gutterIconEnumValues(): List<GutterIcon> =
      GutterIcon.values().toList()

    @Suppress("unused") // Used by parameterized test
    @JvmStatic
    fun iconsToEnabledActions(): List<Pair<GutterIcon, Set<Class<out AnAction>>>> {
      val allActions = setOf(
        CompareReceivedWithApproved::class.java,
        ViewReceivedFile::class.java,
        ViewApprovedFile::class.java,
        ApproveReceivedFile::class.java
      )

      val testParameters = listOf(
        MISSING_MISSING to emptySet(),
        MISSING_PRESENT to setOf(ViewApprovedFile::class.java),
        PRESENT_MISSING to setOf(ViewReceivedFile::class.java, ApproveReceivedFile::class.java),
        PRESENT_EMPTY to allActions,
        PRESENT_PRESENT_SAME to allActions,
        PRESENT_PRESENT_DIFFERENT to allActions,
      )

      require(testParameters.size == GutterIcon.values().size) {
        val testParameterIcons = testParameters.map(Pair<GutterIcon, Set<Class<out AnAction>>>::first)
        val missingIconParameters = GutterIcon.values().toSet() - testParameterIcons
        "Please add enabled actions tests for $missingIconParameters"
      }

      return testParameters
    }
  }

  @ParameterizedTest
  @MethodSource("gutterIconEnumValues")
  fun `light icon`(icon: GutterIcon) {
    NamerFactory.withParameters(icon.name).use {
      Approvals.verify(ApprovalTextWriter(svgResource(icon), svgOptions))
    }
  }

  @ParameterizedTest
  @MethodSource("gutterIconEnumValues")
  fun `dark icon`(icon: GutterIcon) {
    NamerFactory.withParameters(icon.name).use {
      Approvals.verify(ApprovalTextWriter(svgResourceDark(icon), svgOptions))
    }
  }

  @ParameterizedTest
  @MethodSource("iconsToEnabledActions")
  fun `enabled actions`(iconToActions: Pair<GutterIcon, Set<Class<AnAction>>>) {
    val (icon, actions) = iconToActions
    assertThat(icon.enabledActions)
      .isEqualTo(actions)
  }

  private fun svgResource(gutterIcon: GutterIcon): String {
    val svgFileName = "${gutterIcon.iconResourceName}.svg"
    return readResourceFile(svgFileName)
  }

  private fun svgResourceDark(gutterIcon: GutterIcon): String {
    val svgFileName = "${gutterIcon.iconResourceName}_dark.svg"
    return readResourceFile(svgFileName)
  }

  private fun readResourceFile(svgFileName: String): String =
    GutterIconTest::class.java.getResource("/icons/$svgFileName")!!.readText()
}
