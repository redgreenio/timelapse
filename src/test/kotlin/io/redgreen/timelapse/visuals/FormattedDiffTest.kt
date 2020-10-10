package io.redgreen.timelapse.visuals

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.visuals.DiffSpan.Blank
import io.redgreen.timelapse.visuals.DiffSpan.Deletion
import io.redgreen.timelapse.visuals.DiffSpan.Insertion
import io.redgreen.timelapse.visuals.DiffSpan.Unmodified
import org.junit.jupiter.api.Test

class FormattedDiffTest {
  @Test
  fun `it should create a formatted diff from raw diff`() {
    // given
    val rawDiff = """
      diff --git a/build.gradle b/build.gradle
      index 4604741..2bc696d 100644
      --- a/build.gradle
      +++ b/build.gradle
      @@ -1,6 +1,6 @@
       buildscript {
         apply from: 'buildscripts/plugins.gradle'
      -  ext.kotlin_version = '1.3.70'
      +  ext.kotlin_version = '1.3.72'
       
         repositories {
           mavenCentral()
    """.trimIndent()

    // when
    val formattedDiff = FormattedDiff.from(rawDiff)

    // then
    assertThat(formattedDiff.spans)
      .containsExactly(
        Unmodified("buildscript {"),
        Unmodified("  apply from: 'buildscripts/plugins.gradle'"),
        Deletion("  ext.kotlin_version = '1.3.70'"),
        Insertion("  ext.kotlin_version = '1.3.72'"),
        Blank,
        Unmodified("  repositories {"),
        Unmodified("    mavenCentral()"),
      )
  }
}
