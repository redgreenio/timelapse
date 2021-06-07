package com.approvaltests.markers

import com.approvaltests.markers.GutterIcon.MISSING_MISSING
import com.approvaltests.markers.GutterIcon.MISSING_PRESENT
import com.approvaltests.markers.GutterIcon.PRESENT_EMPTY
import com.approvaltests.markers.GutterIcon.PRESENT_MISSING
import com.approvaltests.markers.GutterIcon.PRESENT_PRESENT_DIFFERENT
import com.approvaltests.markers.GutterIcon.PRESENT_PRESENT_SAME
import org.approvaltests.Approvals
import org.approvaltests.core.Options
import org.approvaltests.namer.NamerFactory
import org.approvaltests.writers.ApprovalTextWriter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class GutterIconTest {
  private val svgOptions = Options().forFile().withExtension(".svg")

  companion object {
    @Suppress("unused") // Used by parameterized tests
    @JvmStatic
    fun gutterIconEnumValues(): List<GutterIcon> =
      GutterIcon.values().toList()
  }

  @AfterEach
  fun tearDown() {
    NamerFactory.additionalInformation = null
  }

  @ParameterizedTest
  @MethodSource("gutterIconEnumValues")
  fun `light icon`(icon: GutterIcon) {
    NamerFactory.additionalInformation = icon.name
    Approvals.verify(ApprovalTextWriter(svgResource(icon), svgOptions))
  }

  @Nested
  inner class DarkIcon {
    @Test
    fun `missing missing`() {
      Approvals.verify(svgResourceDark(MISSING_MISSING), svgOptions)
    }

    @Test
    fun `missing present`() {
      Approvals.verify(svgResourceDark(MISSING_PRESENT), svgOptions)
    }

    @Test
    fun `present missing`() {
      Approvals.verify(svgResourceDark(PRESENT_MISSING), svgOptions)
    }

    @Test
    fun `present empty`() {
      Approvals.verify(svgResourceDark(PRESENT_EMPTY), svgOptions)
    }

    @Test
    fun `present present (same)`() {
      Approvals.verify(svgResourceDark(PRESENT_PRESENT_SAME), svgOptions)
    }

    @Test
    fun `present present (different)`() {
      Approvals.verify(svgResourceDark(PRESENT_PRESENT_DIFFERENT), svgOptions)
    }
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
