package com.approvaltests.markers

import org.approvaltests.Approvals
import org.approvaltests.core.Options
import org.approvaltests.namer.NamerFactory
import org.approvaltests.writers.ApprovalTextWriter
import org.junit.jupiter.api.AfterEach
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

  @ParameterizedTest
  @MethodSource("gutterIconEnumValues")
  fun `dark icon`(icon: GutterIcon) {
    NamerFactory.additionalInformation = icon.name
    Approvals.verify(ApprovalTextWriter(svgResourceDark(icon), svgOptions))
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
