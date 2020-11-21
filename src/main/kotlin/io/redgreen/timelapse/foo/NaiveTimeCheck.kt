package io.redgreen.timelapse.foo

import java.net.URLClassLoader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.jar.Manifest

object NaiveTimeCheck {
  fun runCheck(project: String) {
    val manifestUrls = (NaiveTimeCheck::class.java.classLoader as URLClassLoader)
      .findResources("META-INF/MANIFEST.MF")

    val manifest = manifestUrls
      .iterator()
      .asSequence()
      .map { Manifest(it.openStream()) }
      .firstOrNull()

    manifest?.let {
      val validTillValue = it.mainAttributes.getValue("Valid-Till")

      val validTillDateTemporalAccessor = try {
        DateTimeFormatter
          .ISO_LOCAL_DATE
          .parse(validTillValue)
      } catch (_: Exception) {
        // Swallow
        null
      }

      val hasValidityExpired = validTillDateTemporalAccessor != null && !LocalDate.now()
        .isBefore(LocalDate.from(validTillDateTemporalAccessor))
      if (hasValidityExpired) {
        throw IllegalStateException("Not a Git directory: $project")
      }
    }
  }
}
